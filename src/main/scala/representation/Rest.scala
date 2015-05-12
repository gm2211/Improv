package representation

object Rest {
  val DEFAULT_DURATION = 0.0
  val DEFAULT_START_TIME = 0.0
}

case class Rest(
    duration: Double = 0.0,
    startTime: Double = 0.0) extends MusicalElement {
  override def getDuration: Double = duration
  override def getStartTime: Double = startTime
}
