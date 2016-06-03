package messages

import akka.actor.ActorRef

trait Message {
  implicit val sender: ActorRef
}
