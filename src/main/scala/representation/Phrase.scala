package representation

import utils.ImplicitConversions.{toEnhancedTraversable, toFasterMutableList}
import utils.functional.{FunctionalUtils, MemoizedFunc}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{NANOSECONDS, TimeUnit}
import scala.math
import scala.util.Try
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

  def apply(musicalElements: List[Traversable[MusicalElement]], tempoBPM: Double): Option[Phrase] = {
    val phrases = musicalElements
      .withFilter(_.nonEmpty)
      .map(new Phrase(tempoBPM = tempoBPM).withMusicalElements)

    phrases match {
      case Nil =>
        None
      case phrase :: Nil =>
        Some(phrase)
      case phraseList =>
        Some(new Phrase(
          musicalElements = phraseList.toList,
          polyphony = true,
          tempoBPM = tempoBPM))
    }
  }

  def computeMaxChordSize(phrase: Phrase): Int = {
    phrase.musicalElements.foldLeft(1){
      case (i, c: Chord) => math.max(i, c.notes.size)
      case (i, _) => i
    }
  }
  
  def split(phrase: Phrase, splitTimeNS: BigInt): (Option[Phrase], Option[Phrase]) = {
    require(! phrase.polyphony && phrase.nonEmpty)
    var curTime = phrase.getStartTimeNS
    val phraseIter = phrase.toIterator
    val leftElements = mutable.MutableList[MusicalElement]()

    do {
      Try(phraseIter.next()).foreach{ elem =>
        leftElements.+=(elem)
        curTime += elem.getDurationNS
      }
    } while (curTime < splitTimeNS || ! phraseIter.hasNext)

    val rightElements = mutable.MutableList[MusicalElement]()

    if (curTime > splitTimeNS) {
      val (leftHalf, rightHalf) = MusicalElement.split(leftElements.last, splitTimeNS)
      leftHalf.foreach(leftElements.updateLast)
      rightHalf.foreach(rightElements.+=)
    }

    if (phraseIter.hasNext) {
      rightElements ++= phraseIter
    }

    (leftElements.nonEmpty.option(phrase.withMusicalElements(leftElements)),
     rightElements.nonEmpty.option(phrase.withMusicalElements(rightElements).withStartTime(splitTimeNS)))
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

  private val duration: MemoizedFunc[(Phrase, TimeUnit), BigInt] =
    FunctionalUtils.memoized((Phrase.computeDuration _).tupled)

  private val startTime: MemoizedFunc[(Phrase, TimeUnit), BigInt] =
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

  /**
   * Shifts all the elements within the phrase so that the first one starts at the new start time
   * and it preserves the gaps between the start times
   * @param startTime New start time for the phrase
   * @param timeUnit Time unit of the new start time
   * @return A phrase with all its elements shifted
   */
  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): Phrase = {
    var curTimeNS: BigInt = timeUnit.toNanos(startTime.toLong)
    val shiftedMusicalElements = ListBuffer[MusicalElement]()
    for (idx <- musicalElements.indices.dropRight(1)) {
      val startTimeDeltaNS = musicalElements(idx + 1).getStartTimeNS - musicalElements(idx).getStartTimeNS
      shiftedMusicalElements += musicalElements(idx).withStartTime(curTimeNS, NANOSECONDS)
      curTimeNS += startTimeDeltaNS
    }
    shiftedMusicalElements += musicalElements.last.withStartTime(curTimeNS, NANOSECONDS)
    copy(musicalElements = shiftedMusicalElements.toList)
  }

  def getMaxChordSize: Int = maxChordSize(this)

  def scaled(durationRatio: BigDecimal, timeUnit: TimeUnit): Phrase = {
    val scaledElems = musicalElements.map { elem =>
      elem.withDuration((BigDecimal(elem.getDuration(timeUnit)) * durationRatio).toBigInt(), timeUnit)
    }

    copy(musicalElements = scaledElems)
  }

  override def getDuration(timeUnit: TimeUnit): BigInt = duration((this, timeUnit))
  override def foreach[U](f: (MusicalElement) => U): Unit = musicalElements.foreach(f)
  override def getStartTime(timeUnit: TimeUnit): BigInt = startTime((this, timeUnit))
}
