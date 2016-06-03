package representation

import scala.concurrent.duration.TimeUnit

object Chord {
  def fromNotes(notes: Note*) = Chord(notes.toList)
}

case class Chord(elements: List[MusicalElement]) extends MusicalElement {
  require(elements.forall(note => note.getDurationNS == elements.head.getDurationNS)) // Require that all notes have the same length
  require(elements.forall(_.getStartTimeNS == elements.head.getStartTimeNS)) // Require that all notes start at the same time
  require(elements.forall(!_.isInstanceOf[Phrase])) // Require that chords do not contain phrases

  override def getDuration(timeUnit: TimeUnit): BigInt =
    elements.head.getDuration(timeUnit)

  override def getStartTime(timeUnit: TimeUnit): BigInt =
    elements.head.getStartTime(timeUnit)

  override def withDuration(duration: BigInt, timeUnit: TimeUnit): Chord =
    copy(elements = elements.map(_.withDuration(duration, timeUnit)))

  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): Chord =
    copy(elements = elements.map(_.withStartTime(startTime, timeUnit)))

  def notes: List[Note] = elements.collect{case note: Note => note}
}
