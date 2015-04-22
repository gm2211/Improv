package instruments

import instruments.InstrumentType._
import org.jfugue.player.Player
import org.jfugue.theory
import representation.{Phrase, MusicalElement, Note}

class JFugueInstrument(override val instrumentType: InstrumentType = PIANO) extends Instrument {
  val player = new Player

  override def play(musicalElement: MusicalElement): Unit = musicalElement match {
    case n: Note =>
      player.play(JFugueUtils.convertNote(n))
    case p: Phrase =>
      p.foreach(play)
  }

}

object JFugueUtils {
  def convertNote(note: Note): theory.Note =
    new theory.Note(s"${note.name.toString}${note.octave}").setDuration(note.duration).setHarmonicNote(true)
}
