package actors.musicians.behaviour

import messages.{Message, SyncMessage}

class SyncMessageReceivedBehaviour extends AIMusicianBehaviour with ReceiveBehaviour {
  override def apply(message: Message): Unit = message match {
    case m: SyncMessage =>
      musician.foreach(_.directorIdentity = Some(m.sender))

      if (musician.exists(_.musicGenre.isDefined)) {
        musician.foreach(_.currentMusicTime = m.time)
        musician.foreach(_.play(m.time))
      }
    case _ =>
  }
}
