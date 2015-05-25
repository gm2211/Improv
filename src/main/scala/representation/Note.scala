package representation


import representation.NoteName.NoteName
import utils.collections.CollectionUtils

import scala.concurrent.duration.{MILLISECONDS, NANOSECONDS, TimeUnit}
import scala.math
import scala.util.Try


object Note {
  /**
   * Returns true if two notes are equal
   * @param note1 First note
   * @param note2 Second note
   * @param octave Consider octave in comparison?
   * @param duration Consider duration in comparison?
   * @return
   */
  def areEqual(
      note1: Note,
      note2: Note,
      octave: Boolean = true,
      duration: Boolean = true) = {
    note1.name == note2.name &&
      note1.intonation == note2.intonation &&
      (! octave || note1.octave == note2.octave) &&
      (! duration || note1.durationNS == note2.durationNS)
  }

  def pitchToOctave(pitch: Int): Int = math.floor(pitch / 12.0).toInt
  
  val MAX_MIDI_PITCH = 127
  val DEFAULT_NAME = NoteName.A
  val DEFAULT_OCTAVE = 4
  val DEFAULT_PITCH = 60
  val DEFAULT_DURATION = 2000
  val DEFAULT_INTONATION = Natural
  val DEFAULT_LOUDNESS = MF
  val DEFAULT_START_TIME = 0

  private val WELL_AUDIBLE_RANGE = 3 to 5

  /**
   * This method converts a string like "Ab", "A#", "Ab3"
   * @param noteString String representing a note
   * @return a tuple of the (name, intonation, octave)
   */
  def parseString(noteString: String): (Option[NoteName], Option[Intonation], Option[Int]) = {
    val regex = "([A-Z]){1}([b#])?(\\d)?".r
    noteString match {
      case regex(name, intonation, octave) =>
        (Try(NoteName.withName(name)).toOption,
          Some(Intonation(intonation)),
          Try(octave.toInt).toOption)

      case _ =>
        (None, None, None)
    }
  }

  def fromString(noteString: String): Note = {
    val (name, intonation, octave) = parseString(noteString)
    Note(name = name.getOrElse(Note.DEFAULT_NAME),
      intonation = intonation.getOrElse(Note.DEFAULT_INTONATION),
      octave = octave.getOrElse(Note.DEFAULT_OCTAVE))
  }

  def genRandNote(): Note = {
    val octave = CollectionUtils.chooseRandom(WELL_AUDIBLE_RANGE).get
    val duration = MILLISECONDS.toNanos(150)
    val intonation = CollectionUtils
      .chooseRandom(List(Flat, Sharp, Natural))
      .getOrElse(Natural)
    val name = CollectionUtils.chooseRandom(List("A", "B", "C", "D", "E", "F", "G")).getOrElse("A")

    Note(
      name = NoteName.withName(name),
      octave = octave,
      durationNS = duration,
      intonation = intonation)
  }
}

object NoteName extends Enumeration {
  type NoteName = Value
  val A, B, C, D, E, F, G = Value
}

case class Note(name: NoteName = Note.DEFAULT_NAME,
  octave: Int = Note.DEFAULT_OCTAVE,
  midiPitch: Int = Note.DEFAULT_PITCH,
  durationNS: BigInt = Note.DEFAULT_DURATION,
  intonation: Intonation = Note.DEFAULT_INTONATION,
  loudness: Loudness = Note.DEFAULT_LOUDNESS,
  startTimeNS: BigInt = Note.DEFAULT_START_TIME) extends MusicalElement {
  require((0 to Note.MAX_MIDI_PITCH) contains midiPitch, s"$midiPitch is not within ${0 to Note.MAX_MIDI_PITCH}")

  def withName(newName: NoteName) = copy(name = newName)

  def withOctave(newOctave: Int) = copy(octave = newOctave)

  def withIntonation(newIntonation: Intonation) = copy(intonation = newIntonation)

  def withLoudness(newLoudness: Loudness) = copy(loudness = loudness)

  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): Note =
    copy(startTimeNS = timeUnit.toNanos(startTime.toLong))

  override def withDuration(duration: BigInt, timeUnit: TimeUnit): Note =
    copy(durationNS = timeUnit.toNanos(duration.toLong))

  override def getDuration(timeUnit: TimeUnit): BigInt = timeUnit.convert(durationNS.toLong, NANOSECONDS)

  override def getStartTime(timeUnit: TimeUnit): BigInt = timeUnit.convert(startTimeNS.toLong, NANOSECONDS)

}

