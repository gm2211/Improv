package actors.musicians.behaviour

import actors.musicians.behaviour.BoredomBehaviour.Bored
import designPatterns.observer.{EventNotification, Observable}
import messages.consensus.BinaryDecision.BinaryDecisionAnswer
import messages.consensus._
import messages.{Message, SyncMessage}

import scala.util.Random

object BoredomBehaviour {
  def getRandBoredomThreshold = Random.nextInt(30)

  def getRandExtremeBoredomThreshold = 31 + Random.nextInt(10)

  case object Bored extends EventNotification

}

class BoredomBehaviour(
    val boredomThreshold: Double = BoredomBehaviour.getRandBoredomThreshold,
    val extremeBoredomThreshold: Double = BoredomBehaviour.getRandExtremeBoredomThreshold,
    private val boredomFunc: Double => Double = (b: Double) => scala.math.pow(2, b))
      extends AIMusicianBehaviour with ReceiveBehaviour with Observable {
  var boredom = 0.0

  override def apply(message: Message): Unit = message match {
    case sync: SyncMessage =>
      boredom = boredomFunc(sync.time)
      println(s"boredom level: $boredom; threshold: $extremeBoredomThreshold")

    case VoteRequest(sender, decisionType) =>
      decisionType match {
        case Termination =>
          var vote: BinaryDecisionAnswer = BinaryDecision.No
          if (boredom > boredomThreshold) {
            vote = BinaryDecision.Yes
          }
          musician.foreach(m => sender ! VoteResponse(m.self, Termination, vote))
      }
    case c: ConsensusMessage =>
      () // Ignore
    case _ =>
      if (boredom > extremeBoredomThreshold) {
        musician.foreach(_.notify(Bored))
      }
  }

}
