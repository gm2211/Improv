package actors.musicians.behaviour

import actors.musicians.AIMusician

abstract class AIMusicianBehaviour extends ActorBehaviour {
  protected var musician: Option[AIMusician] = None
  def registerMusician(musician: AIMusician) = this.musician = Some(musician)
}
