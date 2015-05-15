package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.{data => jmData}
import jm.util.Read
import org.slf4j.LoggerFactory
import representation._
import utils.ImplicitConversions.{toEnhancedTraversable, toFasterMutableList}
import utils.collections.CollectionUtils

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

  override def getPhrases(partNum: Int): Traversable[Phrase] = {
    val multiVoicePhrase = JMusicParserUtils.convertPart(score.getPart(partNum))
    split(JMusicParserUtils.mergePhrases(multiVoicePhrase).getOrElse(Phrase()))
  }

  override def getMultiVoicePhrases(partNum: Int): Traversable[Phrase] =
    split(JMusicParserUtils.convertPart(score.getPart(partNum)))

  override def getPartIndexByInstrument: mutable.MultiMap[InstrumentType, Int] = {
    val partsArray = score.getPartArray
    (0 until partsArray.size)
      .groupByMultiMap(index => InstrumentType.classify(partsArray(index).getInstrument))
  }

  override def getInstrumentsCounts: Map[InstrumentType.InstrumentCategory, Int] = {
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
   * @param jmNotes Iterable of stuff that either contains, is or can be converted to a jmData.Note
   * @param getNote function that extracts or converts the elements of the iterable to a jmData.Note
   * @tparam A Type that is, contains or can be converted to a jmData.Note (e.g. Tup2[Double, jmData.Note])
   * @return An optional musical element
   */
  def convertNotes[A](jmNotes: List[A], getNote: A => jmData.Note = identity _): Option[MusicalElement] = {
    jmNotes.flatMap(elem => convertNote(getNote(elem))) match {
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

    if (jmNote.isRest)
      Some(Rest(jmNote.getDuration * durationRatio))
    else {
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

  def getNotesByStartTime(phrase: Phrase): mutable.MultiMap[Double, MusicalElement] =
    phrase.musicalElements.groupByMultiMap[Double](_.getStartTime)

  def getNotesByStartTime(phrase: jmData.Phrase): mutable.MultiMap[Double, jmData.Note] =
    phrase.getNoteList.groupByMultiMap[Double](note => note.getNoteStartTime.orElse(0.0))

  def convertPart(part: jmData.Part): Phrase = {
    val phrases = part.getPhraseList.map(convertPhrase)
    val startTime: Double = phrases.collectFirst { case phrase => phrase.startTime }.getOrElse(Phrase.DEFAULT_START_TIME)
    new Phrase(
      musicalElements = phrases.toList,
      polyphony = true,
      tempoBPM = part.getTempo,
      startTime = startTime)
  }

  def convertPhrase(phrase: jmData.Phrase): Phrase = {
    val elements = phrase.getNoteList.flatMap(convertNote)
    new Phrase(elements.toList, tempoBPM = phrase.getTempo, startTime = phrase.getStartTime)
  }

  def mergePhrases(phrase: Phrase): Option[Phrase] =
    phrase.polyphony.option(mergePhrases(phrase.musicalElements.asInstanceOf[List[Phrase]]))

  def mergePhrases(phrases: Traversable[Phrase]): Phrase = {
    def isActive(time: Double, start: Double, end: Double): Boolean = start < time && time < end

    val notesByStartTime = CollectionUtils.mergeMultiMaps(phrases.toList: _*)(getNotesByStartTime)
    val phraseElements = mutable.MutableList[MusicalElement]()
    var activeNotes: List[(Double, Double, MusicalElement)] = List()
    val endTimes = notesByStartTime.flatMap { case (startTime, notes) => notes.map(_.getDuration + startTime) }

    for (time <- notesByStartTime.keySet.toList.++(endTimes).sorted) {
      activeNotes.foreach(elem => phraseElements += elem._3)

      activeNotes = activeNotes.filter { case (start, end, _) => isActive(time, start, end) }
      activeNotes ++= notesByStartTime.getOrElse(time, Set()).map(note => (time, time + note.getDuration, note)).toList
    }

    require(activeNotes.isEmpty)
    Phrase().withMusicalElements(phraseElements)
  }

  def splitPhrase(phrase: Phrase): Phrase = {
    def addToPhrase(element: MusicalElement, phrase: mutable.MutableList[MusicalElement]): Unit = {
      if (phrase.isEmpty) {
        phrase += element
      } else {
        val previousElem = phrase.last
        (previousElem, element) match {
          case (previousNote: Note, note: Note) if Note.areEqual(note, previousNote, duration = false) =>
            phrase.updateLast(previousNote.withDuration(previousNote.duration + note.duration))
          case (previousRest: Rest, rest: Rest) =>
            phrase.updateLast(Rest(previousRest.duration + rest.duration))
          case _ =>
            phrase += element
        }
      }
    }

    var activeElements = List[MusicalElement]()
    val phrasesElements = (0 until phrase.getMaxChordSize).map(i => (i, mutable.MutableList[MusicalElement]())).toList

    for (musicalElement <- phrase) {
      musicalElement match {
        case chord: Chord =>
          activeElements = chord.notes
        case elem =>
          activeElements = List(elem)
      }
      phrasesElements.foreach { case (index, phraseElems) =>
        val elem = Try {
          activeElements(index)
        }.toOption.getOrElse(Rest(musicalElement.getDuration))
        addToPhrase(elem, phraseElems)
      }
    }
    val phrases = phrasesElements.map{ case (index, phraseElems) => new Phrase(phraseElems.toList) }
    Phrase()
      .withMusicalElements(phrases)
      .withPolyphony()
      .getOrElse(phrase)
  }
}

