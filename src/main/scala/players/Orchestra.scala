package players

import java.util.concurrent.Executors

import akka.actor.{ActorRef, ActorSystem, Cancellable}
import messages.Message

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration
import scala.language.implicitConversions

class Orchestra(val name: String = "orchestra") {
  implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
  val system: ActorSystem = ActorSystem.create(name)
  private val director: Director  = new SimpleDirector(system)
  private var scheduledTask: Option[Cancellable] = None

  def registerMusician(musician: ActorRef): Unit = {
    system.eventStream.subscribe(musician, classOf[Message])
  }

  def start(): Unit = {
    schedule(0L, Some(1000L), director)
  }

  private def schedule(delayMS: Long,
                       intervalMS: Option[Long] = None,
                       task: Runnable): Option[Cancellable] = {
    val delay = FiniteDuration(delayMS/1000, "s")
    if (intervalMS.isDefined) {
      val duration = FiniteDuration(intervalMS.get/1000, "s")
      scheduledTask = Option(system.scheduler.schedule(delay, duration, task))
    } else {
      scheduledTask = Option(system.scheduler.scheduleOnce(delay, task))
    }
    return scheduledTask
  }

  def shutdown(): Unit = {
    system.log.debug("Shutting down..")
    system.shutdown()
  }

  def pause(): Unit = scheduledTask.foreach(_.cancel())

  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }
  def shutdown(delay: Long): Unit = {
    schedule(delayMS = delay, task = () => shutdown())
  }

}
