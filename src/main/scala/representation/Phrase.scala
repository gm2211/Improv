package representation

import utils.ImplicitConversions.toEnhancedTraversable
import utils.functional.{FunctionalUtils, MemoizedFunc}

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{NANOSECONDS, TimeUnit}
import scala.math
import scalaz.Scalaz._

object Phrase {
  val DEFAULT_START_TIME = 0.0

  def computeDuration(phrase: Phrase, timeUnit: TimeUnit): BigInt = {
    if (phrase.polyphony)
      phrase.map(p => computeDuration(p.asInstanceOf[Phrase], timeUnit)).max
    else {
      val duration: BigInt = phrase.sumBy(0, _.getDuration(timeUnit))
      duration
    }
  }

  def computeStartTime(phrase: Phrase, timeUnit: TimeUnit): BigInt =
    phrase.minBy(_.getStartTime(timeUnit)).getStartTime(timeUnit)

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

  def split(phrase: Phrase, splitTimeNS: BigInt): (Option[Phrase], Option[Phrase]) = phrase match {
    case p@Phrase(_, true, _) =>
      splitPolyphonic(p, splitTimeNS)
    case p@Phrase(_, false, _) =>
      splitNonPolyphonic(p, splitTimeNS)
  }

  def splitPolyphonic(phrase: Phrase, splitTimeNS: BigInt): (Option[Phrase], Option[Phrase]) = {
    require(phrase.polyphony)
    val left = ListBuffer[Phrase]()
    val right = ListBuffer[Phrase]()

    for (curPhrase <- phrase) {
      val (lPhrase, rPhrase) = split(curPhrase.asInstanceOf[Phrase], splitTimeNS)
      lPhrase.foreach(left.+=)
      rPhrase.foreach(right.+=)
    }

    (left.nonEmpty.option(phrase.withMusicalElements(left)),
      right.nonEmpty.option(phrase.withMusicalElements(right)))
  }

  def splitNonPolyphonic(phrase: Phrase, splitTimeNS: BigInt): (Option[Phrase], Option[Phrase]) = {
    require(! phrase.polyphony && phrase.nonEmpty)
    var curTimeNS = phrase.getStartTimeNS
    val leftElements = ListBuffer[MusicalElement]()
    val rightElements = ListBuffer[MusicalElement]()

    for (elem <- phrase) {
      val newTimeNS = curTimeNS + elem.getDurationNS

      if (curTimeNS <= splitTimeNS) {
        if (newTimeNS > splitTimeNS) {
          val (left, right) = MusicalElement.split(elem, splitTimeNS)
          left.foreach(leftElements.+=)
          right.foreach(rightElements.+=)
        } else {
          leftElements += elem
        }
      } else {
        rightElements += elem
      }

      curTimeNS = newTimeNS
    }

    (leftElements.nonEmpty.option(phrase.withMusicalElements(leftElements).withStartTime(0)),
     rightElements.nonEmpty.option(phrase.withMusicalElements(rightElements).withStartTime(0)))
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
    (! polyphony || canHavePolyphony).option(myCopy(polyphony = polyphony))

  def withMusicalElements(musicalElements: Traversable[MusicalElement]) =
    myCopy(musicalElements = musicalElements.toList)

  def withMusicalElements(musicalElements: MusicalElement*) =
    myCopy(musicalElements = musicalElements.toList)

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
    val shiftedMusicalElements = getShiftedElements(startTime, timeUnit)
    myCopy(musicalElements = shiftedMusicalElements)
  }

  private def getShiftedElements(startTime: BigInt, timeUnit: TimeUnit): List[MusicalElement] = {
    if (polyphony) {
      return musicalElements.map(_.withStartTime(startTime, timeUnit))
    }

    val shiftedMusicalElements = ListBuffer[MusicalElement]()
    var curTimeNS: BigInt = timeUnit.toNanos(startTime.toLong)
    for (idx <- musicalElements.indices.dropRight(1)) {
      val startTimeDeltaNS = musicalElements(idx + 1).getStartTimeNS - musicalElements(idx).getStartTimeNS
      shiftedMusicalElements += musicalElements(idx).withStartTime(curTimeNS, NANOSECONDS)
      curTimeNS += startTimeDeltaNS
    }
    shiftedMusicalElements += musicalElements.last.withStartTime(curTimeNS, NANOSECONDS)
    shiftedMusicalElements.toList
  }

  def getMaxChordSize: Int = maxChordSize(this)

  def scaled(durationRatio: BigDecimal, timeUnit: TimeUnit): Phrase = {
    val scaledElems = musicalElements.map { elem =>
      elem.withDuration((BigDecimal(elem.getDuration(timeUnit)) * durationRatio).toBigInt(), timeUnit)
    }

    myCopy(musicalElements = scaledElems)
  }

  override def getDuration(timeUnit: TimeUnit): BigInt = duration((this, timeUnit))
  override def foreach[U](f: (MusicalElement) => U): Unit = musicalElements.foreach(f)
  override def getStartTime(timeUnit: TimeUnit): BigInt = startTime((this, timeUnit))

  private def myCopy(
      musicalElements: List[MusicalElement] = musicalElements,
      polyphony: Boolean = polyphony,
      tempoBPM: Double = tempoBPM): Phrase = {
    new Phrase(musicalElements = musicalElements,
               polyphony = polyphony,
               tempoBPM = tempoBPM)
  }
}
