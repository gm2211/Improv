package actors.directors

import akka.actor.{Actor, ActorSystem}
import utils.builders.{Count, IsOnce, Once}

trait Director extends Actor {
  def start(): Unit

  def stop(): Unit
}

trait DirectorBuilder[A <: Count] {
  def build[T <: A : IsOnce]: Director

  def withActorSystem(actorSystem: Option[ActorSystem]): DirectorBuilder[Once]
}

