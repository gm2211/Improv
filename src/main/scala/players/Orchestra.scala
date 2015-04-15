package players

import akka.actor.{ActorRef, ActorSystem, Cancellable}
import messages.Message

import scala.language.implicitConversions

class Orchestra(val name: String = "orchestra") {
  val system: ActorSystem = ActorSystem.create(name)
  private val director: Director  = new SimpleDirector(system)
  private var scheduledDirector: Option[Cancellable] = None

  def registerMusician(musician: ActorRef): Unit = {
    system.eventStream.subscribe(musician, classOf[Message])
  }

  def start(): Unit = {
    scheduledDirector = ActorUtils.schedule(system, 0L, Some(1000L), director)
  }


  def shutdown(): Unit = {
    system.log.debug("Shutting down..")
    system.shutdown()
  }

  def pause(): Unit = scheduledDirector.foreach(_.cancel())

  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }
  def shutdown(delay: Long): Unit = {
    ActorUtils.schedule(system, delayMS = delay, task = () => shutdown())
  }

}
