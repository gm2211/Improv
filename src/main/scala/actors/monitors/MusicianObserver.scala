package actors.monitors

import actors.monitors.MusicianObserver.NewMessageEvent
import actors.musicians.Musician
import designPatterns.observer.{EventNotification, Observable, Observer}
import messages.Message
import representation.Phrase

object MusicianObserver {
  def apply(observer: Observer) = {
    val messageObserver = new MusicianObserver
    messageObserver.addObserver(observer)
    messageObserver
  }

  case class NewMessageEvent(message: Message) extends EventNotification
}

class MusicianObserver extends Musician with Observable {
  override def receive: Receive = {
    case m: Message => 
      notifyObservers(NewMessageEvent(m))
  }

  override def play(phrase: Phrase): Unit = ()
}
