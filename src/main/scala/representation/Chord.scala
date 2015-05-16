package representation

object Chord {
  def fromNotes(notes: Note*) = Chord(notes.toList)
}

case class Chord(notes: List[Note]) extends MusicalElement {
  override def getDuration: BigDecimal = notes.head.duration
  override def getStartTime: BigDecimal = notes.head.startTime

  override def withDuration(duration: BigDecimal): MusicalElement =
    copy(notes = notes.map(_.withDuration(duration)))

  override def withStartTime(startTime: BigDecimal): MusicalElement =
    copy(notes = notes.map(_.withStartTime(startTime)))
}
