package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.{data => jmData}
import jm.util.Read
import org.slf4j.LoggerFactory
import representation._
import utils.ImplicitConversions.toEnhancedTraversable
import utils.collections.CollectionUtils

import scala.collection.mutable
import collection.JavaConversions._
import scala.math

object JMusicMIDIParser extends MIDIParserFactory {
  override def apply(filename: String, phraseLength: Int) = {
    val score: jmData.Score = new jmData.Score()
    Read.midi(score, filename)
    new JMusicMIDIParser(score, phraseLength)
  }
}

class JMusicMIDIParser(val score: jmData.Score, val phraseLength: Int) extends MIDIParser {

  def split(phrase: Phrase): Iterator[Phrase] = {
    //TODO Split by length
    List(phrase).iterator
  }

  override def getPhrases(partNum: Int): Iterator[Phrase] = {
    split(JMusicParserUtils.mergePhrases(score.getPart(partNum)))
  }

  override def getPhrase(partNum: Int, phraseNum: Int): Option[Phrase] = {
    //    val phrases = Try{ score.getPart(partNum).getPhraseArray.toList }.toOption
    //    phrases.get.head.get
    //    val notes: List[MusicalElement] = {
    //      phrase.map { p =>
    //        p.getNoteArray.map{ note =>
    //          JMusicParserUtils.convertNote(note).getOrElse(Note()) }.toList
    //      }.getOrElse(List())
    //    }
    //
    //    Phrase.builder.withMusicalElements(notes).build
    None
  }

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
        musicalElements.collect{ case n: Note => n } match {
          case Nil =>
            None
          case note :: Nil =>
            Some(note)
          case notes =>
            Some(Chord(notes))
        }
    }
  }

  def convertNote(jmNote: jmData.Note): Option[MusicalElement] = {
    if (jmNote.isRest)
      Some(Rest(jmNote.getDuration))
    else {
      val notePitch = if (!jmNote.getPitchType) jmNote.getPitch else jmData.Note.freqToMidiPitch(jmNote.getFrequency)
      val intonation = if (jmNote.isFlat) Flat else if (jmNote.isSharp) Sharp else Natural
      val (noteName, _, _) = Note.parseString(jmNote.getNote)

      noteName.flatMap(name => Some(Note(name = name,
        octave = Note.pitchToOctave(notePitch),
        duration = jmNote.getDuration,
        intonation = intonation)))
    }
  }

  def getNotesByStartTime(phrase: jmData.Phrase): mutable.MultiMap[Double, jmData.Note] = {
    var index = -1
    phrase.getNoteList.groupByMultiMap[Double](note => {index += 1; phrase.getNoteStartTime(index)})
  }

  def mergePhrases(part: jmData.Part): Phrase = {
    def isActive(time: Double, start: Double, end: Double): Boolean = start < time && time < end

    //TODO: include bars
    val notesByStartTime = CollectionUtils.mergeMultiMaps(part.getPhraseList.toList:_*)(getNotesByStartTime)
    val phraseBuilder = Phrase.builder
    var activeNotes: List[(Double, Double, jmData.Note)] = List()
    val endTimes = notesByStartTime.flatMap{ case (startTime, notes) => notes.map(_.getDuration + startTime) }

    for (time <- notesByStartTime.keySet.toList.++(endTimes).sorted) {
      convertNotes(activeNotes, (a: (_, _, jmData.Note)) => a._3)
        .foreach(phraseBuilder.addMusicalElement)

      activeNotes = activeNotes.filter{ case (start, end, _) => isActive(time, start, end)}
      activeNotes ++= notesByStartTime.getOrElse(time, Set()).map(note => (time, time + note.getDuration, note)).toList
    }

    require(activeNotes.isEmpty)
    phraseBuilder.build
  }
}

