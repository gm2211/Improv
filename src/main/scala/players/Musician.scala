package players

import akka.actor.Actor
import representation.Note

import scala.language.implicitConversions

trait Musician extends Actor {
  def play(note: Note): Unit
}
