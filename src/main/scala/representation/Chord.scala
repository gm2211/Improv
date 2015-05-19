package representation

import scala.concurrent.duration.TimeUnit

object Chord {
  def fromNotes(notes: Note*) = Chord(notes.toList)
}

case class Chord(notes: List[Note]) extends MusicalElement {
  require(notes.forall(note => note.getDurationNS == notes.head.getDurationNS)) // Require that all notes have the same length
  require(notes.forall(_.getStartTimeNS == notes.head.getStartTimeNS)) // Require that all notes start at the same time

  override def getDuration(timeUnit: TimeUnit): BigInt =
    notes.head.getDuration(timeUnit)

  override def getStartTime(timeUnit: TimeUnit): BigInt =
    notes.head.getStartTime(timeUnit)

  override def withDuration(duration: BigInt, timeUnit: TimeUnit): Chord =
    copy(notes = notes.map(_.withDuration(duration, timeUnit)))

  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): Chord =
    copy(notes = notes.map(_.withStartTime(startTime, timeUnit)))
}
