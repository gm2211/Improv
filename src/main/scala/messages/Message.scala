package messages

import akka.actor.ActorRef

trait Message {
  val sender: ActorRef
}
