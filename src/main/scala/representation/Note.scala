package representation


import representation.NoteName.NoteName
import utils.CollectionUtils

import scala.util.{Random, Try}


object Note {
  def pitchToOctave(pitch: Int): Int = math.floor(pitch / 12.0).toInt

  val MAX_OCTAVE: Int = 8
  val MAX_DURATION: Int = 1

  /**
   * This method converts a string like "Ab", "A#", "Ab3"
   * @param noteString String representing a note
   * @return note represented by the string
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

  def genRandNote(): Note = {
    val octave = Random.nextInt(MAX_OCTAVE)
    val duration = Random.nextInt(MAX_DURATION)
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

case class Note( name:       NoteName   = NoteName.A,
                 octave:     Int        = 1,
                 duration:   Double     = 1.0,
                 intonation: Intonation = Natural     ) extends MusicalElement

