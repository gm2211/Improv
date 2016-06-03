package messages

import akka.actor.ActorRef

case class FinishedPlaying(sender: ActorRef) extends Message
