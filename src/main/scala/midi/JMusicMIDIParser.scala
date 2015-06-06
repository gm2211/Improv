package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.{data => jmData}
import jm.util.Read
import midi.segmentation.PhraseSegmenter
import org.slf4j.LoggerFactory
import representation._
import utils.ImplicitConversions.{toEnhancedIterable, toFasterMutableList}
import utils.collections.CollectionUtils
import utils.functional.FunctionalUtils

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.concurrent.duration.{NANOSECONDS, TimeUnit}
import scala.util.Try
import scalaz.Scalaz._

object JMusicMIDIParser extends MIDIParserFactory {
  override def apply(filename: String) = {
    val score: jmData.Score = new jmData.Score()
    Read.midi(score, filename)
    new JMusicMIDIParser(score, PhraseSegmenter.getDefault())
  }
}

class JMusicMIDIParser(
  val score: jmData.Score,
  val phraseSplitter: PhraseSegmenter) extends MIDIParser {

  def split(phrase: Phrase): Traversable[Phrase] = {
    phraseSplitter.split(phrase)
  }

  private val getPhrasesM = FunctionalUtils.memoized[Int, Traversable[Phrase]]((partNum: Int) => {
    val multiVoicePhrase = JMusicParserUtils.convertPart(score.getPart(partNum))
    multiVoicePhrase.flatMap(phrase => JMusicParserUtils.mergePhrases(phrase).map(split)).getOrElse(List())
  })

  override def getPhrases(partNum: Int): Traversable[Phrase] = getPhrasesM(partNum)

  private val getMultiVoicePhrasesM = FunctionalUtils.memoized((partNum: Int) => {
    val multiVoicePhrase = JMusicParserUtils.convertPart(score.getPart(partNum))
    multiVoicePhrase.map(split).getOrElse(List())
  })

  override def getMultiVoicePhrases(partNum: Int): List[Phrase] = getMultiVoicePhrasesM(partNum).toList

  private val partIndexByInstrumentM = FunctionalUtils.memoized[mutable.MultiMap[InstrumentType, Int]]({
    val partsArray = score.getPartArray
    (0 until partsArray.size)
      .groupByMultiMap(index => InstrumentType.classify(partsArray(index).getInstrument))
  })

  override def getPartIndexByInstrument: mutable.MultiMap[InstrumentType, Int] = partIndexByInstrumentM

  def getInstrumentsCounts: Map[InstrumentType.InstrumentCategory, Int] = {
    val parts = score.getPartArray
    val instruments = parts.map(i => InstrumentType.classify(i.getInstrument))
    instruments.groupBy(identity).mapValues(_.length)
  }
}

object JMusicParserUtils {
  private val log = LoggerFactory.getLogger(getClass)

  def getTempo(part: jmData.Part): Double = {
    Try(part.getTempo)
      .filter(_ >= 0)
      .orElse(Try(part.getMyScore.getTempo))
      .getOrElse(MusicalElement.DEFAULT_TEMPO_BPM)
  }

  def getTempo(phrase: jmData.Phrase): Double = {
    Try(phrase.getTempo)
      .filter(_ >= 0)
      .getOrElse(getTempo(phrase.getMyPart))
  }

  /**
   * Takes an iterable and a function that converts (or extracts) its elements to a jmData.Note and returns an optional
   * musical element
   * @param musicalElements Iterable of stuff that either contains, is or can be converted to a jmData.Note
   * @param endTime Time at which all notes terminate
   * @return An optional musical element
   */
  def mergeNotes(
      musicalElements: List[MusicalElement],
      endTime: BigInt,
      timeUnit: TimeUnit = NANOSECONDS): Option[MusicalElement] = {
    def computeDuration(em: MusicalElement) = timeUnit.toNanos(endTime.toLong) - em.getStartTimeNS
    musicalElements match {
      case Nil =>
        None
      case element :: Nil =>
        Some(element.withDuration(computeDuration(element)))
      case elements =>
        elements.collect { case n: Note => n } match {
          case Nil =>
            Some(elements.head.withDuration(computeDuration(elements.head)))
          case note :: Nil =>
            Some(note.withDuration(computeDuration(note)))
          case notes =>
            Some(Chord(notes.map(note => note.withDuration(computeDuration(note)))))
        }
    }
  }

  def convertNote(jmNote: jmData.Note): Option[MusicalElement] = {
    val durationRatio = 1.0 / 4.0 // In JMusic the duration of 1.0 represents a quarter note (CROTCHET)
    val tempoBPM = getTempo(jmNote.getMyPhrase)
    val fromBPM = (value: Double) => MusicalElement.fromBPM(value * durationRatio, tempoBPM)
    val duration = fromBPM(jmNote.getDuration)
    val startTime = fromBPM(jmNote.getNoteStartTime.get)

    if (jmNote.isRest) {
      Some(
        Rest()
          .withDuration(duration)
          .withStartTime(startTime)
      )
    } else {
      val notePitch = jmNote.getPitchType match {
        case jmData.Note.MIDI_PITCH =>
          jmNote.getPitch
        case _ =>
          jmData.Note.freqToMidiPitch(jmNote.getFrequency)
      }
      val intonation = if (jmNote.isFlat) Flat else if (jmNote.isSharp) Sharp else Natural
      val (noteName, _, _) = Note.parseString(jmNote.getNote)
      val loudness = Loudness(jmNote.getDynamic)

      noteName.flatMap(name => Some(Note(name = name,
        octave = Note.pitchToOctave(notePitch),
        midiPitch = notePitch,
        durationNS = duration,
        accidental = intonation,
        loudness = loudness,
        startTimeNS = startTime)))
    }
  }

  val getNotesByStartTimeNS = FunctionalUtils.memoized { (phrase: Phrase) =>
    phrase.musicalElements.groupByMultiMap[BigInt](_.getStartTime(NANOSECONDS))
  }

  val convertPart = FunctionalUtils.memoized((part: jmData.Part) => {
    val phrases = part.getPhraseList.map(convertPhrase)
    Phrase(phrases.toList, getTempo(part))
  })

  val convertPhrase = FunctionalUtils.memoized((phrase: jmData.Phrase) => {
    val elements = mutable.MutableList[MusicalElement]()
    val jmNotes = phrase.getNoteList.toList.sortBy(_.getNoteStartTime.get)

    for (jmNote <- jmNotes) {
      val convertedNote = convertNote(jmNote)
      convertedNote.foreach(addToPhraseElements(_, elements))
    }

    new Phrase(elements.toList, tempoBPM = getTempo(phrase))
  })

  def mergePhrases(phrase: Phrase): Option[Phrase] =
    phrase.polyphony.option(mergePhrases(phrase.musicalElements.asInstanceOf[List[Phrase]]))

  def mergePhrases(phrases: Traversable[Phrase]): Phrase = {
    val notesByStartTime = CollectionUtils.mergeMultiMaps(phrases.toList: _*)(getNotesByStartTimeNS)
    val phraseElements = mutable.MutableList[MusicalElement]()
    var activeNotes: List[MusicalElement] = List()
    val endTimes = notesByStartTime.flatMap { case (startTime, notes) =>
      notes.map(note => startTime + note.getDurationNS)
    }
    val times = notesByStartTime.keySet.toList.++(endTimes).distinct.sorted

    for (time <- times) {
      mergeNotes(activeNotes, time).foreach(addToPhraseElements(_, phraseElements))

      activeNotes = activeNotes.flatMap(MusicalElement.resizeIfActive(time, _))
      activeNotes ++= notesByStartTime.getOrElse(time, Set()).toList
    }

    require(activeNotes.isEmpty, "All active notes must be consumed")
    Phrase().withMusicalElements(phraseElements)
  }

  private def addToPhraseElements(element: MusicalElement, phraseElements: mutable.MutableList[MusicalElement]): Unit = {
    if (phraseElements.isEmpty) {
      if (element.getStartTimeNS > 0) {
        phraseElements += new Rest(durationNS = element.getStartTimeNS)
      }
      phraseElements += element
    } else {
      val previousElem = phraseElements.last
      (previousElem, element) match {
        case (previousNote: Note, note: Note) if Note.areEqual(note, previousNote, duration = false) =>
          phraseElements.updateLast(previousNote.withDuration(previousNote.durationNS + note.durationNS))
        case (previousRest: Rest, rest: Rest) =>
          phraseElements.updateLast(previousRest.withDuration(previousRest.durationNS + rest.durationNS))
        case _ =>
          if (element.getStartTimeNS > phraseElements.last.getEndTimeNS + 1) {
            phraseElements += new Rest(startTimeNS = phraseElements.last.getEndTimeNS)
                                    .withEndTime(element.getStartTimeNS - 1)
          }
          phraseElements += element
      }
    }
  }

  def splitPhrase(phrase: Phrase): Phrase = {
    var activeElements = List[MusicalElement]()
    val phrasesElements = (0 until phrase.getMaxChordSize).map(i => (i, mutable.MutableList[MusicalElement]())).toList
    val musicalElements: List[MusicalElement] = phrase.musicalElements.sortBy(_.getStartTimeNS)

    for (musicalElement <- musicalElements) {
      musicalElement match {
        case chord: Chord =>
          activeElements = chord.notes
        case elem =>
          activeElements = List(elem)
      }
      phrasesElements.foreach { case (index, phraseElems) =>
        val elem = Try {
          activeElements(index)
        }.toOption.getOrElse(Rest(durationNS = musicalElement.getDurationNS, startTimeNS = musicalElement.getStartTimeNS))
        addToPhraseElements(elem, phraseElems)
      }
    }

    val phrases = phrasesElements.map { case (index, phraseElems) => new Phrase(phraseElems.toList) }
    Phrase()
      .withMusicalElements(phrases)
      .withPolyphony()
      .getOrElse(phrase)
  }

}

