package actors.musicians

import java.util.UUID

import akka.actor.Actor
import representation.MusicalElement


trait Musician extends Actor {
  def play(musicalElement: MusicalElement): Unit
}
