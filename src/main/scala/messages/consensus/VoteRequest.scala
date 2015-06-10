package messages.consensus

import akka.actor.ActorRef

case class VoteRequest(
  override val sender: ActorRef, 
  decisionType: DecisionType) extends ConsensusMessage

