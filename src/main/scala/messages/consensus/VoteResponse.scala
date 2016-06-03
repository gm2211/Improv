package messages.consensus

import akka.actor.ActorRef

case class VoteResponse(
  override val sender: ActorRef,
  decisionType: DecisionType,
  decision: DecisionAnswer) extends ConsensusMessage
