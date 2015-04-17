package actors.musicians

import akka.actor.Actor

import scala.language.implicitConversions

trait Musician extends Actor {
  def play(): Unit
}
