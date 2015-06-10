package messages.consensus

import akka.actor.ActorRef

case class FinalDecision(override val sender: ActorRef, decision: DecisionAnswer) extends ConsensusMessage
