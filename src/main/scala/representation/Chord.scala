package representation

object Chord {
  def fromNotes(notes: Note*) = Chord(notes.toList)
}

case class Chord(notes: List[Note]) extends MusicalElement {
  override def getDuration: Double = notes.head.duration
  override def getStartTime: Double = notes.head.startTime
}
