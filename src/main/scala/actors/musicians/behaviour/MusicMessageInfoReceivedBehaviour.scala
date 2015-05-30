package actors.musicians.behaviour

import messages.{MusicInfoMessage, Message}

class MusicMessageInfoReceivedBehaviour extends AIMusicianBehaviour with ReceiveBehaviour {
  override def apply(message: Message): Unit = message match {
    case m: MusicInfoMessage =>
      musician.foreach(_.musicInfoMessageCache.addBinding(m.time, m))
    case _ =>
  }
}
