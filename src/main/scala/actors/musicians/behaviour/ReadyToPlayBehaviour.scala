package actors.musicians.behaviour

import messages.Message
import messages.consensus.BinaryDecision.{Yes, No}
import messages.consensus.{VoteResponse, ReadyToPlay, VoteRequest}

class ReadyToPlayBehaviour extends AIMusicianBehaviour with ReceiveBehaviour {
  override def apply(message: Message): Unit = message match {
    case VoteRequest(director, r@ReadyToPlay) =>
      musician.foreach { mus =>
        val ready = if (mus.readyToPlay) Yes else No
        director ! VoteResponse(mus.self, ReadyToPlay, ready)
      }

    case _ =>
  }
}
