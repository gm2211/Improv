package actors.directors

import akka.actor.{ActorSystem, Cancellable}
import messages.SyncMessage
import utils.ActorUtils
import utils.ImplicitConversions._

class SimpleDirector(val actorSystem: ActorSystem,
                     val syncFrequencyMS: Long = 1000) extends Director {
  var task: Option[Cancellable] = None
  var tickCount: Long = 0

  override def start(): Unit = {
    ActorUtils.schedule(
      actorSystem,
      delayMS = 0,
      intervalMS = syncFrequencyMS,
      task = () => sync())
  }

  override def stop(): Unit = {
    task.foreach(_.cancel())
  }


  def sync(): Unit = {
    ActorUtils.broadcast(actorSystem, SyncMessage(tickCount))
    tickCount += 1
  }
}
