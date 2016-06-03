package messages

import akka.actor.ActorRef

case class Start(override val sender: ActorRef) extends Message
