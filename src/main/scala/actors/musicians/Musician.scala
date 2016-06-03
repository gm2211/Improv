package actors.musicians

import akka.actor.Actor
import representation.Phrase


trait Musician extends Actor {
  def play(phrase: Phrase): Unit
}
