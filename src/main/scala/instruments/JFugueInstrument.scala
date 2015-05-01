package instruments

import instruments.InstrumentType.{PIANO, InstrumentType}
import org.jfugue.player.Player
import org.jfugue.theory
import representation.{MusicalElement, Note, Phrase, Rest}

class JFugueInstrument(override val instrumentType: InstrumentType = PIANO()) extends Instrument {
  val player = new Player

  override def play(musicalElement: MusicalElement): Unit = musicalElement match {
    case note: Note =>
      val convertedNotePattern = JFugueUtils.convertNote(note).getPattern
      convertedNotePattern.setInstrument(instrumentType.instrumentNumber)
      player.play(convertedNotePattern)
    case r: Rest =>
      Thread.sleep(r.duration.toInt)
    case p: Phrase =>
      p.foreach(play)
  }

}

object JFugueUtils {
  def convertNote(note: Note): theory.Note =
    new theory.Note(s"${note.name.toString}").setDuration(note.duration/5)
}
