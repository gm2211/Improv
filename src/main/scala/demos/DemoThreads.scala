package demos

import instruments.{Instrument, JFugueInstrument}
import utils.ImplicitConversions.anyToRunnable

object DemoThreads {
  def run() = {
    val i1: Instrument = new JFugueInstrument()
    val i2: Instrument = new JFugueInstrument()

    val p1: Runnable = () => i1.play(representation.Note.fromString("C5b C5b C5b C5b C5b C5b C5b C5b"))
    val p2: Runnable = () => i2.play(representation.Note.fromString("D5b D5b D5b D5b D5b D5b D5b D5b"))

    val t1 = new Thread(p1)
    val t2 = new Thread(p2)

    t1.run()
    t2.run()
  }
}
