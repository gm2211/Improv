package messages

import akka.actor.ActorRef

case class SyncMessage(override val sender: ActorRef, time: Long) extends Message {
}
