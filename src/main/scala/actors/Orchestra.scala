package actors

import akka.actor.{ActorRef, ActorSystem}
import messages.Message
import actors.directors.{SimpleDirector, Director}
import utils.ActorUtils
import utils.ImplicitConversions._

import scala.language.implicitConversions

class Orchestra(val name: String = "orchestra") {
  val system: ActorSystem = ActorSystem.create(name)
  private val director: Director  = new SimpleDirector(system)

  def registerMusician(musician: ActorRef): Unit = {
    system.eventStream.subscribe(musician, classOf[Message])
  }

  def start(): Unit = {
    director.start()
  }

  def shutdown(): Unit = {
    system.log.debug("Shutting down..")
    system.shutdown()
  }

  def pause(): Unit = director.stop()

  def shutdown(delay: Long): Unit = {
    ActorUtils.schedule(system, delayMS = delay, task = () => shutdown())
  }
}
