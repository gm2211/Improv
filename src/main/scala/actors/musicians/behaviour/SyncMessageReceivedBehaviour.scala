package actors.musicians.behaviour

import messages.{Message, SyncMessage}

class SyncMessageReceivedBehaviour extends AIMusicianBehaviour with ReceiveBehaviour {
  override def apply(message: Message): Unit = message match {
    case m: SyncMessage =>
      musician.foreach(_.currentMusicTime = m.time)
      musician.foreach(_.play(m.time))
    case _ =>
  }
}
