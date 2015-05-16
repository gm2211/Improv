package representation


import java.util.UUID

import representation.NoteName.NoteName
import utils.collections.CollectionUtils

import scala.math
import scala.util.{Random, Try}


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
      (! duration || note1.duration == note2.duration)
  }

  def pitchToOctave(pitch: Int): Int = math.floor(pitch / 12.0).toInt

  val MAX_OCTAVE = 8

  val DEFAULT_NAME = NoteName.A
  val DEFAULT_OCTAVE = 4
  val DEFAULT_PITCH: Int = 60
  val DEFAULT_DURATION = 1.0
  val DEFAULT_INTONATION = Natural
  val DEFAULT_LOUDNESS = MF
  val DEFAULT_START_TIME: Double = 0.0

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
    val duration = Random.nextDouble() + 0.5
    val intonation = CollectionUtils
      .chooseRandom(List(Flat, Sharp, Natural))
      .getOrElse(Natural)
    val name = CollectionUtils.chooseRandom(List("A", "B", "C", "D", "E", "F", "G")).getOrElse("A")

    Note(
      name = NoteName.withName(name),
      octave = octave,
      duration = duration,
      intonation = intonation)
  }
}

object NoteName extends Enumeration {
  type NoteName = Value
  val A, B, C, D, E, F, G = Value
}

case class Note(name: NoteName = Note.DEFAULT_NAME,
  octave: Int = Note.DEFAULT_OCTAVE,
  pitch: Int = Note.DEFAULT_PITCH,
  duration: Double = Note.DEFAULT_DURATION,
  intonation: Intonation = Note.DEFAULT_INTONATION,
  loudness: Loudness = Note.DEFAULT_LOUDNESS,
  startTime: Double = Note.DEFAULT_START_TIME) extends MusicalElement {

  def withName(newName: NoteName) = copy(name = newName)

  def withOctave(newOctave: Int) = copy(octave = newOctave)

  def withDuration(newDuration: Double) = copy(duration = newDuration)

  def withIntonation(newIntonation: Intonation) = copy(intonation = newIntonation)

  def withLoudness(newLoudness: Loudness) = copy(loudness = loudness)

  override def withStartTime(startTime: Double): Note = copy(startTime = startTime)

  override def getDuration: Double = duration

  override def getStartTime: Double = startTime

}

