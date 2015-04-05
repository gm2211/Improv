package players

import akka.actor.{ActorLogging, Props}
import instruments.{DemoInstrument, Instrument}
import messages.SyncMessage

object AIMusician {
  def props(instrument: Instrument = new DemoInstrument): Props = Props(new AIMusician(instrument))
}

class AIMusician(var instrument: Instrument = new DemoInstrument) extends Musician with ActorLogging {
  override def play(): Unit = instrument.play()

  override def receive = {
    case m: SyncMessage =>
      log.info("Received Sync")
      log.debug("instrument {}", instrument)
      play()
    case _ =>
      log.info("Received unknown message")

  }

}
