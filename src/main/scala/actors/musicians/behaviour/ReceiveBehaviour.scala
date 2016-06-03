package actors.musicians.behaviour

import messages.Message

trait ReceiveBehaviour extends ActorBehaviour with Function[Message, Unit] {
}
