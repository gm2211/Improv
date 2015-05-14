package representation

import utils.ImplicitConversions.toEnhancedTraversable
import utils.functional.{FunctionalUtils, MemoizedFunc}

import scala.math
import scalaz.Scalaz._

object Phrase {
  val DEFAULT_TEMPO_BPM = 120
  val DEFAULT_START_TIME = 0.0

  def computeDuration(phrase: Phrase): Double = phrase.numericFold(0.0, _.getDuration)

  def apply(): Phrase = {
    new Phrase()
  }

  def computeMaxChordSize(phrase: Phrase): Int = {
    phrase.musicalElements.foldLeft(1){
      case (i, c: Chord) => math.max(i, c.notes.size)
      case (i, _) => i
    }
  }
}

/**
 *
 * @param musicalElements List of musical elements that belong to the phrase
 * @param polyphony All the phrases in this phrases are to be played at the same time
 *                  (Pre: all the musical elements in the phrase must be phrases)
 * @param tempoBPM Tempo of the phrase
 */
case class Phrase(
  musicalElements: List[MusicalElement] = List(),
  polyphony: Boolean = false,
  tempoBPM: Double = Phrase.DEFAULT_TEMPO_BPM,
  startTime: Double = Phrase.DEFAULT_START_TIME)
    extends MusicalElement with Traversable[MusicalElement] {
  private val maxChordSize: MemoizedFunc[Phrase, Int] = FunctionalUtils.memoized(Phrase.computeMaxChordSize)
  private val duration: MemoizedFunc[Phrase, Double] = FunctionalUtils.memoized(Phrase.computeDuration)

  require(! polyphony || canHavePolyphony)

  private def canHavePolyphony: Boolean =
    musicalElements.forall{ case p: Phrase => true; case _ => false }

  def withPolyphony(polyphony: Boolean = true): Option[Phrase] =
    (! polyphony || canHavePolyphony).option(copy(polyphony = polyphony))

  def withMusicalElements(musicalElements: Traversable[MusicalElement]) =
    copy(musicalElements = musicalElements.toList)

  def withMusicalElements(musicalElements: MusicalElement*) =
    copy(musicalElements = musicalElements.toList)

  def getMaxChordSize: Int = maxChordSize(this)

  override def getDuration: Double = duration(this)
  override def isEmpty: Boolean = musicalElements.isEmpty
  override def foreach[U](f: (MusicalElement) => U): Unit = musicalElements.foreach(f)
  override def getStartTime: Double = startTime
}
