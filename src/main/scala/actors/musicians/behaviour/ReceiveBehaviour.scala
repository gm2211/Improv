package actors.musicians.behaviour

import messages.Message

trait ReceiveBehaviour extends ActorBehaviour {
  def apply(message: Message): Unit
}
