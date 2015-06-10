package messages.consensus

import messages.consensus.BinaryDecision.{No, Yes}

trait DecisionType

trait DecisionAnswer
object InvalidDecision extends DecisionAnswer

object BinaryDecision {
  sealed trait BinaryDecisionAnswer extends DecisionAnswer
  case object Yes extends BinaryDecisionAnswer
  case object No extends BinaryDecisionAnswer
}
trait BinaryDecision extends DecisionType

object Decisions {
  val default: PartialFunction[Iterable[DecisionAnswer], DecisionAnswer] = { case _ => InvalidDecision }
  val unanimousDecision: PartialFunction[Iterable[DecisionAnswer], DecisionAnswer] = {
    case answers: Iterable[DecisionAnswer] =>
      val unanimousYes = answers.forall{ case Yes => true; case _ => false }

      if (unanimousYes) Yes else No
  }

  def decide(answers: Iterable[DecisionAnswer]): DecisionAnswer = {
    val decisionFunctions = unanimousDecision orElse default
    decisionFunctions(answers)
  }
}



case object Termination extends BinaryDecision
