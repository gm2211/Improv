package messages

import akka.actor.ActorRef

case class Stop(override val sender: ActorRef) extends Message
