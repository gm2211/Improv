package actors.directors

import akka.actor.{ActorLogging, Actor, ActorSystem}
import messages.consensus._
import messages.{Start, Stop}
import utils.ActorUtils
import utils.builders.{AtLeastOnce, Count, IsAtLeastOnce}
import utils.collections.CollectionUtils

trait Director extends Actor with ActorLogging {
  implicit val actorSystem: ActorSystem
  val votesByDecisionType = CollectionUtils.createHashMultimap[DecisionType, VoteResponse]
  def start(): Unit
  def stop(): Unit

  protected def haveAllActorsVoted(decisionType: DecisionType): Boolean

  override def receive: Receive = {
    case Start =>
      start()

    case Stop =>
      stop()

    case VoteRequest(sender, decisionType) =>
      if (sender.path != self.path) {
        log.debug(s"Received request for $decisionType")
        ActorUtils.broadcast(VoteRequest(self, decisionType))
      }

    case voteResponse: VoteResponse =>
      log.debug(s"Received response $voteResponse")
      votesByDecisionType.addBinding(voteResponse.decisionType, voteResponse)

      if (haveAllActorsVoted(voteResponse.decisionType)) {
        val votes = votesByDecisionType.get(voteResponse.decisionType).get.map(_.decision)
        val decision = Decisions.decide(votes)

        votesByDecisionType.remove(voteResponse.decisionType)
        ActorUtils.broadcast(FinalDecision(self, decision))
        processDecision(voteResponse.decisionType, decision)
      }
  }

  private def processDecision(decisionType: DecisionType, decision: DecisionAnswer) = decisionType match {
    case Termination =>
      decision match {
        case BinaryDecision.Yes =>
          stop()
          actorSystem.shutdown()
        case _ =>
      }
    case _ =>

  }
}

trait DirectorBuilder[A <: Count] {
  def build[T <: A : IsAtLeastOnce]: Director
  def withActorSystem(actorSystem: ActorSystem): DirectorBuilder[AtLeastOnce]
}
