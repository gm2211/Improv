package actors

import actors.directors.{Director, SimpleDirector}
import actors.musicians.Musician
import akka.actor.{ActorSystem, Props}
import messages.Message
import utils.ActorUtils
import utils.ImplicitConversions.anyToRunnable

import scala.language.implicitConversions

class Orchestra(val name: String = "orchestra") {
  val system: ActorSystem = ActorSystem.create(name)
  private val director: Director  = new SimpleDirector(system)

  def registerMusician(musician: => Musician): Unit = {
    val actor = system.actorOf(Props(musician))
    system.eventStream.subscribe(actor, classOf[Message])
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
    ActorUtils.scheduleOnce(system, delay, () => shutdown())
  }
}
