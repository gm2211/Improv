package players

import akka.actor.ActorLogging
import instruments.Instrument
import messages.SyncMessage
import representation.{Phrase, Note}

class AIMusician( val instrument: Instrument,
                  composer1: Option[Composer] = None) extends Musician with ActorLogging {
  val composer = composer1.getOrElse(new RandomComposer)
  override def play(): Unit = {
    composer.compose(Phrase.builder.build()).foreach(instrument.play)
  }

  override def receive = {
    case m: SyncMessage =>
      log.debug("Received Sync")
      log.debug("instrument {}", instrument)
      play()
  }
}
