package representation


import representation.NoteName.NoteName
import utils.CollectionUtils

import scala.util.{Random, Try}


object Note {

  def pitchToOctave(pitch: Int): Int = math.floor(pitch / 12.0).toInt

  val MAX_OCTAVE = 8

  val DEFAULT_NAME = NoteName.A
  val DEFAULT_OCTAVE = 4
  val DEFAULT_DURATION = 1.0
  val DEFAULT_INTONATION = Natural

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

    Note(NoteName.withName(name), octave, duration, intonation)
  }
}

object NoteName extends Enumeration {
  type NoteName = Value
  val A, B, C, D, E, F, G = Value
}

case class Note(name: NoteName = Note.DEFAULT_NAME,
  octave: Int = Note.DEFAULT_OCTAVE,
  duration: Double = Note.DEFAULT_DURATION,
  intonation: Intonation = Note.DEFAULT_INTONATION) extends MusicalElement {
  def withName(newName: NoteName) = copy(name = newName)

  def withOctave(newOctave: Int) = copy(octave = newOctave)

  def withDuration(newDuration: Double) = copy(duration = newDuration)

  def withIntonation(newIntonation: Intonation) = copy(intonation = newIntonation)
}

