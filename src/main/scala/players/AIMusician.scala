package players

import akka.actor.{ActorLogging, Props}
import instruments.OvertoneInstrumentType.OvertoneInstrumentType
import instruments.{Instrument, OvertoneInstrument}
import messages.SyncMessage
import representation.Note

object AIMusician {
  def props(instrument: Option[Instrument] = None): Props = Props(new AIMusician(instrument.getOrElse(new OvertoneInstrument)))
  def props(instrument: Instrument): Props = props(Option(instrument))
  def props(instrumentType: OvertoneInstrumentType): Props = {
    val instrument = new OvertoneInstrument(instrumentType = Some(instrumentType))
    return Props(new AIMusician(instrument))
  }
}

class AIMusician(var instrument: Instrument = new OvertoneInstrument) extends Musician with ActorLogging {
  override def play(note: Note): Unit = {
    instrument.play(note)
  }

  override def receive = {
    case m: SyncMessage =>
      log.debug("Received Sync")
      log.debug("instrument {}", instrument)
      play(Note.genRandNote())
  }
}
