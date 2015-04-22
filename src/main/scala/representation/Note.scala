package representation

import utils.CollectionUtils

import scala.util.Random

object Note {
  val MAX_OCTAVE: Int = 8
  val MAX_DURATION: Int = 1


  def genRandNote(): Note = {
    val octave = Random.nextInt(MAX_OCTAVE)
    val duration = Random.nextInt(MAX_DURATION)
    val intonation = CollectionUtils
      .chooseRandom(List(Flat, Sharp, Natural))
      .getOrElse(Natural)
    val note = CollectionUtils.chooseRandom(List(A, B, C, D, E, F, G)).getOrElse(A)

    note(octave, duration, intonation)
  }
}

sealed trait Note extends MusicalElement {
  def name: String
  def octave: Int
  def duration: Int
  def intonation: Intonation
}
case class A(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "A" }
case class B(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "B" }
case class C(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "C" }
case class D(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "D" }
case class E(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "E" }
case class F(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "F" }
case class G(octave: Int, duration: Int, intonation: Intonation) extends Note { val name = "G" }

