package representation

case class Rest(duration: Double) extends MusicalElement {
  override def getDuration: Double = duration
}
