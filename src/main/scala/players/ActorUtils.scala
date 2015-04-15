package players

import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{ActorSystem, Cancellable}
import messages.SyncMessage

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object ActorUtils {
  implicit val executionContext = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def broadcast(system: ActorSystem, message: SyncMessage): Unit = system.eventStream.publish(message)

  def schedule( system: ActorSystem,
                delayMS: Long,
                intervalMS: Option[Long] = None,
                task: Runnable): Option[Cancellable] = {
    val delay = FiniteDuration(delayMS, TimeUnit.MILLISECONDS)

    if (intervalMS.isDefined) {
      val duration = FiniteDuration(intervalMS.get, TimeUnit.MILLISECONDS)
      return Some(system.scheduler.schedule(delay, duration, task))
    } else {
      return Some(system.scheduler.scheduleOnce(delay, task))
    }
  }
}
