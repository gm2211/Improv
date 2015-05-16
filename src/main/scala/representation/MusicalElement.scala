package representation

trait MusicalElement {
  def withStartTime(startTime: Double): MusicalElement
  def withDuration(duration: Double): MusicalElement
  def getDuration: Double
  def getStartTime: Double
}
