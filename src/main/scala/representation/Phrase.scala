package representation

import utils.ImplicitConversions.toEnhancedTraversable
import utils.functional.{FunctionalUtils, MemoizedFunc}

import scala.concurrent.duration.TimeUnit
import scala.math
import scalaz.Scalaz._

object Phrase {
  val DEFAULT_START_TIME = 0.0

  def computeDuration(phrase: Phrase, timeUnit: TimeUnit): BigInt =
    phrase.sumBy(0, _.getDuration(timeUnit))

  def computeStartTime(phrase: Phrase, timeUnit: TimeUnit): BigInt =
    phrase.minBy(_.getDuration(timeUnit)).getDuration(timeUnit)

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
  tempoBPM: Double = MusicalElement.DEFAULT_TEMPO_BPM)
    extends MusicalElement with Traversable[MusicalElement] {
  private val maxChordSize: MemoizedFunc[Phrase, Int] =
    FunctionalUtils.memoized(Phrase.computeMaxChordSize)

  private val durationMS: MemoizedFunc[(Phrase, TimeUnit), BigInt] =
    FunctionalUtils.memoized((Phrase.computeDuration _).tupled)

  private val startTimeMS: MemoizedFunc[(Phrase, TimeUnit), BigInt] =
    FunctionalUtils.memoized((Phrase.computeStartTime _).tupled)

  require(! polyphony || canHavePolyphony)

  private def canHavePolyphony: Boolean =
    musicalElements.forall{ case p: Phrase => true; case _ => false }

  def withPolyphony(polyphony: Boolean = true): Option[Phrase] =
    (! polyphony || canHavePolyphony).option(copy(polyphony = polyphony))

  def withMusicalElements(musicalElements: Traversable[MusicalElement]) =
    copy(musicalElements = musicalElements.toList)

  def withMusicalElements(musicalElements: MusicalElement*) =
    copy(musicalElements = musicalElements.toList)

  override def withDuration(newDuration: BigInt, timeUnit: TimeUnit): Phrase =
    scaled(BigDecimal(newDuration / getDuration(timeUnit)), timeUnit)

  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): Phrase = {
    val shiftedMusicalElements = musicalElements.map(_.withStartTime(startTime, timeUnit))
    copy(musicalElements = shiftedMusicalElements)
  }

  def getMaxChordSize: Int = maxChordSize(this)

  def scaled(durationRatio: BigDecimal, timeUnit: TimeUnit): Phrase = {
    val scaledElems = musicalElements.map { elem =>
      elem.withDuration((BigDecimal(elem.getDuration(timeUnit)) * durationRatio).toBigInt(), timeUnit)
    }

    copy(musicalElements = scaledElems)
  }

  override def getDuration(timeUnit: TimeUnit): BigInt = durationMS((this, timeUnit))
  override def foreach[U](f: (MusicalElement) => U): Unit = musicalElements.foreach(f)
  override def getStartTime(timeUnit: TimeUnit): BigInt = startTimeMS((this, timeUnit))
}
