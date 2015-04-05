package players

import akka.actor.Actor
import org.slf4j.{LoggerFactory, Logger}

trait Musician extends Actor {
  def play(): Unit
}
