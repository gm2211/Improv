package demos

import instruments.InstrumentType._
import org.jfugue.rhythm.Rhythm
import utils.ImplicitConversions.anyToRunnable

object DemoJFugue {
  def run() = {
    import org.jfugue.player.Player
    import org.jfugue.theory.ChordProgression
    val runnable1 = () => {
      val player: Player = new Player
      println("runnable1 before play")
      player.play(new ChordProgression("I V VI II VI V IV I").getPattern.setInstrument(PIANO().instrumentNumber))
      println("runnable1 after play")
      Thread.sleep(5000)

    }
    val runnable2 = () => {
      val player: Player = new Player
      println("runnable2 before play")
      player.play(new Rhythm("Oo.`.oOo.`").getPattern.repeat(2).setInstrument(PERCUSSIVE().instrumentNumber))
      println("runnable2 after play")
      Thread.sleep(5000)
    }
    new Thread(runnable1).start()
    new Thread(runnable2).start()
  }
}
