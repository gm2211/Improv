package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.{data => jmData}
import jm.util.Read
import org.slf4j.LoggerFactory
import representation._

object MIDIParser {
  def apply(filename: String) = {
    val score: jmData.Score = new jmData.Score()
    Read.midi(score, filename)
    new MIDIParser(score)
  }
}

class MIDIParser(val score: jmData.Score) {
  /**
   * Returns the index of the provided part in the score
   * @param part part
   * @return index of the part in the score
   */
  def getPartIndex(part: jmData.Part): Option[Int] = {
    val index = score.getPartArray.toList.indexOf(part)
    if (index >= 0) Some(index) else None
  }

  def getPhrases(partNum: Int): Iterator[Option[Phrase]] = {
    val phrases = score.getPart(partNum).getPhraseArray
    val phraseIterator: Iterator[Option[Phrase]] = new Iterator[Option[Phrase]] {
      val phrasesIterator = phrases.iterator
      override def hasNext: Boolean = phrasesIterator.hasNext

      override def next(): Option[Phrase] = {
        if (hasNext) {
          val musElems = phrasesIterator.next()
            .getNoteArray
            .map{note => println(s"note: $note");JMUtils.convertNote(note).getOrElse(Note())}
          return Some(Phrase.builder.withMusicalElements(musElems).build())
        }
        None
      }
    }

    phraseIterator
  }

  def getPhrase(phraseNum: Int, partNum: Int): Phrase = {
    val phrase = Option(score.getPart(partNum).getPhrase(phraseNum))
    val notes: List[MusicalElement] = phrase.map(
      p => p.getNoteArray.map(note => JMUtils.convertNote(note).getOrElse(Note())).toList
    ).getOrElse(List())

    Phrase.builder.withMusicalElements(notes).build()
  }

  def getPartIndexByInstrument: Map[InstrumentType, Array[Int]] =
    score.getPartArray.groupBy(p => InstrumentType.classify(p.getInstrument))
      .mapValues(parts => parts.map(part => getPartIndex(part).getOrElse(-1)))

  def getInstrumentsCounts: Map[InstrumentType.InstrumentCategory, Int] = {
    val parts = score.getPartArray
    val instruments = parts.map(i => InstrumentType.classify(i.getInstrument))
    instruments.groupBy(identity).mapValues(_.length)
  }
}

object JMUtils {
  val log = LoggerFactory.getLogger(getClass)

  def convertNote(jmNote: jmData.Note): Option[MusicalElement] = {
    if (jmNote.isRest)
      Some(Rest(jmNote.getDuration))
    else {
      val notePitch = if (!jmNote.getPitchType) jmNote.getPitch else jmData.Note.freqToMidiPitch(jmNote.getFrequency)
      val intonation = if (jmNote.isFlat) Flat else if (jmNote.isSharp) Sharp else Natural
      val (noteName, _, _)  = Note.parseString(jmNote.getNote)

      noteName.flatMap(name => Some(Note(name = name,
        octave = Note.pitchToOctave(notePitch),
        duration = jmNote.getDuration,
        intonation = intonation)))
    }
  }
}
