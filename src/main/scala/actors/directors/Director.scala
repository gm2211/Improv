package actors.directors

import akka.actor.{Actor, ActorSystem}
import utils.builders.{Count, IsAtLeastOnce, AtLeastOnce}

trait Director extends Actor {
  def start(): Unit

  def stop(): Unit
}

trait DirectorBuilder[A <: Count] {
  def build[T <: A : IsAtLeastOnce]: Director
  def withActorSystem(actorSystem: ActorSystem): DirectorBuilder[AtLeastOnce]
}

