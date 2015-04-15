package players

import akka.actor.ActorSystem
import messages.SyncMessage

class SimpleDirector(val actorSystem: ActorSystem) extends Director {
  override def sync(): Unit = ActorUtils.broadcast(actorSystem, SyncMessage())
}
