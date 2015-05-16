package representation

object Rest {
  val DEFAULT_DURATION = 0.0
  val DEFAULT_START_TIME = 0.0
}

case class Rest(
    duration: BigDecimal = 0.0,
    startTime: BigDecimal = 0.0) extends MusicalElement {
  override def getDuration: BigDecimal = duration
  override def getStartTime: BigDecimal = startTime

  override def withDuration(duration: BigDecimal): MusicalElement = copy(duration = duration)

  override def withStartTime(startTime: BigDecimal): MusicalElement = copy(startTime = startTime)
}
