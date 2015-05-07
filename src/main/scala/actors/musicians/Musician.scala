package actors.musicians

import akka.actor.Actor
import representation.MusicalElement


trait Musician extends Actor {
  def play(musicalElement: MusicalElement): Unit
}
