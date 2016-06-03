package genetic

sealed trait StoppingCriteria

case class IterationLimitReached(limit: Int) extends StoppingCriteria
