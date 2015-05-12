package midi

import java.util
import javax.sound.midi.Sequence

import jm.midi.MidiUtil
import jm.music.data.{Part, Score}
import jm.music.{data => jmData}
import representation.{MusicalElement, Note, Phrase, Rest}

import scala.collection.JavaConversions._


object MIDIConverter {
  def toSequence(phrase: Phrase): Sequence = {
    toSequence(JMusicConverterUtils.toPhrase(phrase))
  }

  def toSequence(phrase: jmData.Phrase): Sequence = {
    MidiUtil.scoreToSeq(JMusicConverterUtils.wrapInScore(phrase))
  }

}

object JMusicConverterUtils {

  def convertNote(note: Note): jmData.Note = {
    val jmNote = new jmData.Note()
    val duration: Double = note.duration / 8
    jmNote.setDuration(duration)
    jmNote.setRhythmValue(duration / jmData.Note.DEFAULT_DURATION_MULTIPLIER)
    jmNote.setPitchType(jmData.Note.MIDI_PITCH)
    jmNote.setPitch(note.pitch)
    jmNote.setDynamic(note.loudness.loudness)
    jmNote
  }

  def convertRest(rest: Rest): jmData.Note =
    new jmData.Rest(rest.getDuration / 8)

  def toNote(musicalElement: MusicalElement): Option[jmData.Note] = musicalElement match {
    case note: Note =>
      Some(convertNote(note))
    case rest: Rest =>
      Some(convertRest(rest))
    case _ =>
      None
  }

  def toPart(multiVoicePhrase: Phrase): jmData.Part = {
    require(multiVoicePhrase.polyphony)
    val part = new jmData.Part()
    val phrases = multiVoicePhrase.musicalElements.collect{ case phrase: Phrase => toPhrase(phrase) }
    phrases.foreach{ phrase => phrase.setMyPart(part); part.add(phrase) }
    part
  }

  def toPhrase(phrase: Phrase): jmData.Phrase = {
    require(! phrase.polyphony)
    val jmPhrase = new jmData.Phrase()
    val startTimeOrdering = Ordering.by((elem: MusicalElement) => elem.getStartTime)
    jmPhrase.setDuration(phrase.getDuration)
    jmPhrase.setStartTime(phrase.getStartTime)
    jmPhrase.setTempo(phrase.tempoBPM)

    phrase.musicalElements
      .sorted(startTimeOrdering)
      .flatMap(toNote)
      .foreach{ note => note.setMyPhrase(jmPhrase); jmPhrase.add(note) }

    jmPhrase
  }

  def wrapInScore(jmPhrase: jmData.Phrase): Score = {
    val part = new Part()
    jmPhrase.setMyPart(part)
    part.addPhrase(jmPhrase)
    val score = new Score()
    part.setMyScore(score)
    score.addPart(part)
    score
  }

  def wrapInScore(jmPart: jmData.Part): Score = {
    val score = new Score()
    jmPart.setMyScore(score)
    score.addPart(jmPart)
    score
  }
}
