package players

import akka.actor.ActorSystem
import messages.SyncMessage

object ActorUtils {
  def broadcast(system: ActorSystem, message: SyncMessage): Unit = system.eventStream.publish(message)
}
