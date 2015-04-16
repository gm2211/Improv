package representation

import utils.CollectionUtils

import scala.util.Random

object Note {
  val MAX_OCTAVE: Int = 8
  object Name extends Enumeration {
    val A, B, C, D, E, F, G = Value
  }

  def genRandNote(): Note = {
    val name   = CollectionUtils.choose(Name.values).getOrElse(Name.A)
    val octave = Random.nextInt(MAX_OCTAVE)
    return new Note(name, octave)
  }
}

case class Note(name: Note.Name.Value, octave: Int = 3) extends MusicalElement {
}
