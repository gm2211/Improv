package midi

import instruments.InstrumentType
import instruments.InstrumentType.InstrumentType
import jm.music.{data => jmData}
import jm.util.Read
import org.slf4j.LoggerFactory
import representation._

import scala.util.Try

object JMusicMIDIParser {
  // TODO: Define exactly what length means
  val DEFAULT_PHRASE_LENGTH = 10

  def apply(filename: String) = {
    val score: jmData.Score = new jmData.Score()
    Read.midi(score, filename)
    new JMusicMIDIParser(score)
  }
}

class JMusicMIDIParser(val score: jmData.Score) extends MIDIParser {
  /**
   * Returns the index of the provided part in the score
   * @param part part
   * @return index of the part in the score
   */
  private def getPartIndex(part: jmData.Part): Option[Int] = {
    val index = score.getPartArray.toList.indexOf(part)
    if (index >= 0) Some(index) else None
  }

  override def getPhrases(partNum: Int): Iterator[Phrase] = {
    Try {
      val phrases = score.getPart(partNum).getPhraseArray
      val phraseIterator: Iterator[Phrase] = new Iterator[Phrase] {
        val phrasesIterator = phrases.iterator
        var nextPhrase: Option[Phrase] = None

        private def computeNext() = {
          if (phrasesIterator.hasNext) {
            val notes = phrasesIterator.next()
              .getNoteArray
              .map { note => JMUtils.convertNote(note).getOrElse(Note()) }

            nextPhrase = Some(Phrase.builder.withMusicalElements(notes).build)
          }
        }

        override def hasNext: Boolean = {
          while (phrasesIterator.hasNext && nextPhrase.isEmpty) {
            computeNext()
          }
          nextPhrase.isDefined
        }

        override def next(): Phrase = {
          val nextPhrase = this.nextPhrase
          this.nextPhrase = None
          nextPhrase.get
        }
      }
      phraseIterator
    }.toOption
  }

  override def getPhrase(phraseNum: Int, partNum: Int): Phrase = {
    val phrase = Option(score.getPart(partNum).getPhrase(phraseNum))
    val notes: List[MusicalElement] = phrase.map(
      p => p.getNoteArray.map(note => JMUtils.convertNote(note).getOrElse(Note())).toList
    ).getOrElse(List())

    Phrase.builder.withMusicalElements(notes).build
  }

  override def getPartIndexByInstrument: Map[InstrumentType, Array[Int]] =
    score.getPartArray.groupBy(p => InstrumentType.classify(p.getInstrument))
      .mapValues(parts => parts.map(part => getPartIndex(part).getOrElse(-1)))

  override def getInstrumentsCounts: Map[InstrumentType.InstrumentCategory, Int] = {
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
      val (noteName, _, _) = Note.parseString(jmNote.getNote)

      noteName.flatMap(name => Some(Note(name = name,
        octave = Note.pitchToOctave(notePitch),
        duration = jmNote.getDuration,
        intonation = intonation)))
    }
  }
}
