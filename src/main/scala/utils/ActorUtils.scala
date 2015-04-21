package utils

import java.util.concurrent.{Executors, TimeUnit}

import akka.actor.{ActorSystem, Cancellable}
import messages.Message

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

object ActorUtils {
  implicit val executionContext = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())

  def broadcast(system: ActorSystem, message: Message): Unit = system.eventStream.publish(message)

  def schedule(system: ActorSystem,
               delayMS: Long,
               intervalMS: Long,
               task: Runnable): Option[Cancellable] = {
    val delay = FiniteDuration(delayMS, TimeUnit.MILLISECONDS)
    val duration = FiniteDuration(intervalMS, TimeUnit.MILLISECONDS)

    Some(system.scheduler.schedule(delay, duration, task))
  }

  def scheduleOnce(system: ActorSystem,
                   delayMS: Long,
                   task: Runnable): Option[Cancellable] = {
    val delay = FiniteDuration(delayMS, TimeUnit.MILLISECONDS)
    Some(system.scheduler.scheduleOnce(delay, task))
  }
}
