package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.{data => jmData}
import jm.util.Read
import org.slf4j.LoggerFactory
import representation._
import utils.ImplicitConversions.{toEnhancedTraversable, toFasterMutableList}
import utils.collections.CollectionUtils
import utils.functional.FunctionalUtils

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.util.Try
import scalaz.Scalaz._

object JMusicMIDIParser extends MIDIParserFactory {
  override def apply(filename: String, phraseLength: Int) = {
    val score: jmData.Score = new jmData.Score()
    Read.midi(score, filename)
    new JMusicMIDIParser(score, phraseLength)
  }
}

class JMusicMIDIParser(val score: jmData.Score, val phraseLength: Int) extends MIDIParser {

  def split(phrase: Phrase): Traversable[Phrase] = {
    //TODO Split by length
    List(phrase)
  }

  private val getPhrasesM = FunctionalUtils.memoized((partNum: Int) => {
    val multiVoicePhrase = JMusicParserUtils.convertPart(score.getPart(partNum))
    split(JMusicParserUtils.mergePhrases(multiVoicePhrase).getOrElse(Phrase()))
  })

  override def getPhrases(partNum: Int): Traversable[Phrase] = getPhrasesM(partNum)

  private val getMultiVoicePhrasesM = FunctionalUtils.memoized((partNum: Int) => {
    val multiVoicePhrase = JMusicParserUtils.convertPart(score.getPart(partNum))
    split(multiVoicePhrase)
  })

  override def getMultiVoicePhrases(partNum: Int): Traversable[Phrase] = getMultiVoicePhrasesM(partNum)

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
  val log = LoggerFactory.getLogger(getClass)

  /**
   * Takes an iterable and a function that converts (or extracts) its elements to a jmData.Note and returns an optional
   * musical element
   * @param musicalElements Iterable of stuff that either contains, is or can be converted to a jmData.Note
   * @param getNote function that extracts or converts the elements of the iterable to a jmData.Note
   * @tparam A Type that is, contains or can be converted to a jmData.Note (e.g. Tup2[Double, jmData.Note])
   * @return An optional musical element
   */
  def mergeNotes(musicalElements: List[MusicalElement]): Option[MusicalElement] = {
    musicalElements match {
      case Nil =>
        None
      case musicalElement :: Nil =>
        Some(musicalElement)
      case musicalElements =>
        musicalElements.collect { case n: Note => n } match {
          case Nil =>
            Some(musicalElements.head)
          case note :: Nil =>
            Some(note)
          case notes =>
            Some(Chord(notes))
        }
    }
  }

  def convertNote(jmNote: jmData.Note): Option[MusicalElement] = {
    val durationRatio = 1.0 / 4.0 // In JMusic the duration of 1.0 represents a quarter note (CROTCHET)

    if (jmNote.isRest) {
      Some(
        Rest(
          duration = jmNote.getDuration * durationRatio,
          startTime = jmNote.getNoteStartTime.orElse(0.0) * durationRatio))
    } else {
      val notePitch = jmNote.getPitchType match {
        case jmData.Note.MIDI_PITCH =>
          jmNote.getPitch
        case _ =>
          jmData.Note.freqToMidiPitch(jmNote.getFrequency)
      }
      val intonation = if (jmNote.isFlat) Flat else if (jmNote.isSharp) Sharp else Natural
      val (noteName, _, _) = Note.parseString(jmNote.getNote)
      val duration = jmNote.getDuration / durationRatio
      val loudness = Loudness(jmNote.getDynamic)
      val startTime = jmNote.getNoteStartTime.orElse(0.0) * durationRatio

      noteName.flatMap(name => Some(Note(name = name,
        octave = Note.pitchToOctave(notePitch),
        pitch = notePitch,
        duration = duration,
        intonation = intonation,
        loudness = loudness,
        startTime = startTime)))
    }
  }

  val getNotesByStartTime = FunctionalUtils.memoized((phrase: Phrase) =>
    phrase.musicalElements.groupByMultiMap[Double](_.getStartTime))

  def getNotesByStartTime(phrase: jmData.Phrase): mutable.MultiMap[Double, jmData.Note] =
    phrase.getNoteList.groupByMultiMap[Double](note => note.getNoteStartTime.orElse(0.0))

  val convertPart = FunctionalUtils.memoized((part: jmData.Part) => {
    val phrases = part.getPhraseList.map(convertPhrase)
    val startTime: Double = Try(phrases.minBy(_.getStartTime).getStartTime).getOrElse(Phrase.DEFAULT_START_TIME)
    new Phrase(
      musicalElements = phrases.toList,
      polyphony = true,
      tempoBPM = part.getTempo,
      startTime = startTime)
  })

  val convertPhrase = FunctionalUtils.memoized((phrase: jmData.Phrase) => {
    val elements = mutable.MutableList[MusicalElement]()
    val jmNotes = phrase.getNoteList.toList.sortBy(_.getNoteStartTime.get)
    jmNotes.foreach(convertNote(_).foreach(addToPhrase(_, elements)))

    new Phrase(elements.toList, tempoBPM = phrase.getTempo, startTime = phrase.getStartTime)
  })

  def mergePhrases(phrase: Phrase): Option[Phrase] =
    phrase.polyphony.option(mergePhrases(phrase.musicalElements.asInstanceOf[List[Phrase]]))

  def mergePhrases(phrases: Traversable[Phrase]): Phrase = {
    def isActive(time: Double, elem: MusicalElement): Boolean =
      elem.getStartTime < time && time < elem.getStartTime + elem.getDuration

    def resizeIfActive(time: Double, elem: MusicalElement): Option[MusicalElement] = {
      isActive(time, elem).option(elem.withStartTime(time).withDuration(time - elem.getStartTime))
    }

    val notesByStartTime = CollectionUtils.mergeMultiMaps(phrases.toList: _*)(getNotesByStartTime)
    val phraseElements = mutable.MutableList[MusicalElement]()
    var activeNotes: List[MusicalElement] = List()
    val endTimes = notesByStartTime.flatMap { case (startTime, notes) => notes.map(_.getDuration + startTime) }
    val times = notesByStartTime.keySet.toList.++(endTimes).distinct.sorted

    for (time <- times) {
      mergeNotes(activeNotes).foreach(addToPhrase(_, phraseElements))

      activeNotes = activeNotes.flatMap(resizeIfActive(time, _))
      activeNotes ++= notesByStartTime.getOrElse(time, Set()).toList
    }

    require(activeNotes.isEmpty)
    Phrase().withMusicalElements(phraseElements)
  }

  private def addToPhrase(element: MusicalElement, phrase: mutable.MutableList[MusicalElement]): Unit = {
    if (phrase.isEmpty) {
      phrase += element
    } else {
      val previousElem = phrase.last
      (previousElem, element) match {
        case (previousNote: Note, note: Note) if Note.areEqual(note, previousNote, duration = false) =>
          phrase.updateLast(previousNote.withDuration(previousNote.duration + note.duration))
        case (previousRest: Rest, rest: Rest) =>
          phrase.updateLast(previousRest.withDuration(previousRest.duration + rest.duration))
        case _ =>
          phrase += element
      }
    }
  }

  def splitPhrase(phrase: Phrase): Phrase = {
    var activeElements = List[MusicalElement]()
    val phrasesElements = (0 until phrase.getMaxChordSize).map(i => (i, mutable.MutableList[MusicalElement]())).toList

    for (musicalElement <- phrase.musicalElements.sortBy(_.getStartTime)) {
      musicalElement match {
        case chord: Chord =>
          activeElements = chord.notes
        case elem =>
          activeElements = List(elem)
      }
      phrasesElements.foreach { case (index, phraseElems) =>
        val elem = Try {
          activeElements(index)
        }.toOption.getOrElse(Rest(duration = musicalElement.getDuration, startTime = musicalElement.getStartTime))
        addToPhrase(elem, phraseElems)
      }
    }

    val phrases = phrasesElements.map { case (index, phraseElems) => new Phrase(phraseElems.toList) }
    Phrase()
      .withMusicalElements(phrases)
      .withPolyphony()
      .getOrElse(phrase)
  }
}

