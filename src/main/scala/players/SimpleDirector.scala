package players

import akka.actor.{Cancellable, ActorSystem}
import utils.ImplicitConversions._
import messages.SyncMessage

class SimpleDirector( val actorSystem: ActorSystem,
                      val syncFrequency: Long = 10) extends Director {
  var task: Option[Cancellable] = None

  override def start(): Unit = {
    ActorUtils.schedule(actorSystem,
      syncFrequency,
      task = () => sync())
  }

  override def stop(): Unit = {
    task.foreach(_.cancel())
  }

  def sync(): Unit = ActorUtils.broadcast(actorSystem, SyncMessage())
}
