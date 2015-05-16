package representation

trait MusicalElement {
  def withStartTime(startTime: BigDecimal): MusicalElement
  def withDuration(duration: BigDecimal): MusicalElement
  def getDuration: BigDecimal
  def getStartTime: BigDecimal
}
