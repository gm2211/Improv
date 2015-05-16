package representation

object Chord {
  def fromNotes(notes: Note*) = Chord(notes.toList)
}

case class Chord(notes: List[Note]) extends MusicalElement {
  override def getDuration: Double = notes.head.duration
  override def getStartTime: Double = notes.head.startTime

  override def withDuration(duration: Double): MusicalElement =
    copy(notes = notes.map(_.withDuration(duration)))

  override def withStartTime(startTime: Double): MusicalElement =
    copy(notes = notes.map(_.withStartTime(startTime)))
}
