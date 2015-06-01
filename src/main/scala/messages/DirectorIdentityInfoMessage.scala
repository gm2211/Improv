package messages

import akka.actor.ActorRef

case class DirectorIdentityInfoMessage(
  override val sender: ActorRef,
  director: ActorRef) extends Message
