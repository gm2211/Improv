package actors.directors

import akka.actor.{Actor, ActorSystem}
import messages.{Stop, Start}
import utils.builders.{Count, IsAtLeastOnce, AtLeastOnce}

trait Director extends Actor {
  implicit val actorSystem: ActorSystem
  def start(): Unit
  def stop(): Unit

  override def receive: Receive = {
    case Start =>
      start()
    case Stop =>
      stop()
  }
}

trait DirectorBuilder[A <: Count] {
  def build[T <: A : IsAtLeastOnce]: Director
  def withActorSystem(actorSystem: ActorSystem): DirectorBuilder[AtLeastOnce]
}
