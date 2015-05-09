package representation

import utils.functional.{MemoizedFunc, FunctionalUtils}
import utils.ImplicitConversions.toEnhancedTraversable

import scala.collection.mutable
import scala.math

object Phrase {
  def computeDuration(phrase: Phrase): Double = phrase.numericFold(0.0, _.getDuration)

  def apply(): Phrase = {
    new Phrase()
  }

  def computeMaxChordSize(phrase: Phrase): Int = {
    phrase.musicalElements.foldLeft(0){
      case (i, c: Chord) => math.max(i, c.notes.size)
      case (i, _) => i
    }
  }
}

case class Phrase(
  musicalElements: mutable.MutableList[MusicalElement] = mutable.MutableList(),
  tempoBPM: Double = 120)
    extends MusicalElement with mutable.Traversable[MusicalElement] {
  private val maxChordSize: MemoizedFunc[Phrase, Int] = FunctionalUtils.memoized(Phrase.computeMaxChordSize)
  private val duration: MemoizedFunc[Phrase, Double] = FunctionalUtils.memoized(Phrase.computeDuration)

  def withMusicalElements(musicalElements: Traversable[MusicalElement]) =
    copy(musicalElements = musicalElements.to[mutable.MutableList])

  def addMusicalElement(musicalElement: MusicalElement) = {
    musicalElements += musicalElement

    musicalElement match {
      case chord: Chord =>
        maxChordSize.update(this, maxChordSize(this) + chord.notes.size)
      case _ =>
    }

    duration.update(this, duration(this) + musicalElement.getDuration)
    this
  }

  def getMaxChordSize: Int = maxChordSize(this)

  override def getDuration: Double = duration(this)
  override def isEmpty: Boolean = musicalElements.isEmpty
  override def foreach[U](f: (MusicalElement) => U): Unit = musicalElements.foreach(f)
}
