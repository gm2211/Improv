package actors.directors

import akka.actor.{ActorSystem, Cancellable}
import messages.SyncMessage
import utils.ActorUtils
import utils.ImplicitConversions._

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
