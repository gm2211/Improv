package actors

import actors.directors.{Director, DirectorBuilder, SimpleDirector}
import actors.musicians.Musician
import akka.actor.{ActorRef, ActorSystem, Props}
import messages.{Stop, Start, Message}
import tests.utils.ActorUtils
import tests.utils.ImplicitConversions.{anyToRunnable, wrapInOption}

import scala.language.implicitConversions

case class OrchestraBuilder(
                             var name: Option[String] = None,
                             var actorSystem: Option[ActorSystem] = None,
                             var director: Option[DirectorBuilder[_]] = None) {
  def withName(name: Option[String]) = copy(name = name)

  def withActorSystem(actorSystem: Option[ActorSystem]) = copy(actorSystem = actorSystem)

  def withDirector(director: Option[DirectorBuilder[_]]) = copy(director = director)

  def build: Orchestra = new Orchestra(this)
}

object Orchestra {
  def builder: OrchestraBuilder = new OrchestraBuilder()
}

class Orchestra(val builder: OrchestraBuilder) {
  val DEFAULT_NAME: String = "orchestra"

  val name: String = builder.name.getOrElse(DEFAULT_NAME)
  implicit val system: ActorSystem = builder.actorSystem.getOrElse(ActorSystem.create(name))
  private val director: ActorRef = {
    system.actorOf(Props(builder.director.getOrElse(SimpleDirector.builder).withActorSystem(system).build))
  }
  ActorUtils.subscribe(director, classOf[Message])


  def registerMusician(musician: => Musician): Unit = {
    val actor = system.actorOf(Props(musician))
    ActorUtils.subscribe(actor, classOf[Message])
  }

  def start(): Unit = {
    director ! Start
  }

  def shutdown(): Unit = {
    system.log.debug("Shutting down..")
    system.shutdown()
  }

  def pause(): Unit = director ! Stop

  def shutdown(delay: Long): Unit = {
    ActorUtils.scheduleOnce(system, delay, () => shutdown())
  }
}

