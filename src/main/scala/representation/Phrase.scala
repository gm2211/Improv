package representation

import java.util.concurrent.TimeUnit

import representation.KeySignature.CMaj

import utils.ImplicitConversions.{toEnhancedIterable,toFasterMutableList}
import utils.collections.CollectionUtils
import utils.functional.{FunctionalUtils, MemoizedFunc}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable
import scala.concurrent.duration.TimeUnit
import scala.concurrent.duration.{NANOSECONDS, TimeUnit}
import scala.language.postfixOps
import scala.math
import scala.util.Try
import scalaz.Scalaz._

object Phrase {
  val DEFAULT_START_TIME = 0.0
  val DEFAULT_KEY_SIGNATURE: KeySignature = CMaj.keySignature

  def apply(): Phrase = {
    new Phrase()
  }

  def createPolyphonicPhrase(
    musicalElements: List[Traversable[MusicalElement]],
    tempoBPM: Double,
    keySignature: KeySignature): Option[Phrase] = {
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
          tempoBPM = tempoBPM,
          keySignature = keySignature))
    }
  }

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

  def computeMelodicIntervals(phrase: Phrase): List[Double] = {
    require(! phrase.polyphony)
    var prevNote: Option[Note] = None
    val intervals = ListBuffer[Double]()
    for (elem <- phrase) {
      elem match {
        case note: Note =>
          if (prevNote.isDefined) {
            intervals += note.midiPitch - prevNote.get.midiPitch
          }
          prevNote = Some(note)
        case _ =>
      }
    }
    intervals.toList
  }

  def computePitchHistogram(phrase: Phrase): Array[Double] = {
    require(! phrase.polyphony)
    val histogram = Array.fill[Double](Note.MAX_MIDI_PITCH + 1)(0)
    for (elem <- phrase) {
      elem match {
        case note: Note =>
          histogram(note.midiPitch) += 1
        case _ =>
      }
    }
    histogram
  }

  def computeStartTimeIntervals(phrase: Phrase): Array[Double] = {
    require(! phrase.polyphony)
    val timesBetweenStartTimes = ListBuffer[Double]()
    var prevElem: Option[MusicalElement] = None
    for (elem <- phrase if ! elem.isInstanceOf[Rest]) {
      if (prevElem.isDefined) {
        timesBetweenStartTimes += elem.getStartTimeBPM(phrase.tempoBPM).toDouble -
          prevElem.get.getStartTimeBPM(phrase.tempoBPM).toDouble
      }
      prevElem = Some(elem)
    }
    timesBetweenStartTimes.toArray
  }


  def computeMaxChordSize(phrase: Phrase): Int = {
    phrase.musicalElements.foldLeft(1){
      case (i, c: Chord) => math.max(i, c.elements.size)
      case (i, _) => i
    }
  }

  def allRest(phrase: Phrase): Boolean = {
    if (phrase.polyphony) {
      phrase.musicalElements.asInstanceOf[List[Phrase]].forall(allRest)
    } else {
      phrase.musicalElements.forall(_.isInstanceOf[Rest])
    }
  }

  def unmerge(phrase: Phrase): Phrase = {
    def elemMatch(e1: Option[MusicalElement], e2: MusicalElement): Boolean = e1.exists{ e11 =>
      (e11, e2) match {
        case (n1: Note, n2: Note) =>
          Note.areEqual(n1, n2, duration = false)
        case (r1: Rest, r2: Rest) =>
          true
        case _ =>
          false
      }
    }

    var activeElements = List[MusicalElement]()
    val phrasesElements: List[mutable.MutableList[MusicalElement]] =
      (0 until phrase.getMaxChordSize).map(i => mutable.MutableList[MusicalElement]()).toList
    val musicalElements: List[MusicalElement] = phrase.musicalElements.sortBy(_.getStartTimeNS)

    for (musicalElement <- musicalElements) {
      musicalElement match {
        case chord: Chord =>
          activeElements = chord.elements
        case elem =>
          activeElements = List(elem)
      }

      val rest = Rest(
        durationNS = musicalElement.getDurationNS,
        startTimeNS = musicalElement.getStartTimeNS)

      val availablePhrases = mutable.HashSet[Int](phrasesElements.indices.toArray:_*)

      activeElements ++= (1 to phrasesElements.size - activeElements.size).map(i => rest)

      activeElements.toStream.foreach{ element =>
        val listIdx = Option(phrasesElements.indexWhere(l => elemMatch(l.lastOption, element)))
          .filter(_ >= 0)
          .getOrElse(CollectionUtils.chooseRandom(availablePhrases).get)

        addToPhraseElements(element, phrasesElements(listIdx))
        availablePhrases.remove(listIdx)
      }
    }

    val phrases = phrasesElements.map(phraseElems => new Phrase(phraseElems.toList))
    Phrase()
      .withMusicalElements(phrases)
      .withPolyphony()
      .getOrElse(phrase)
  }

  val getNotesByStartTimeNS = FunctionalUtils.memoized { (phrase: Phrase) =>
    phrase.musicalElements.groupByMultiMap[BigInt](_.getStartTime(NANOSECONDS))
  }

  /**
   * @param musicalElements Iterable of stuff that either contains, is or can be converted to a jmData.Note
   * @param endTime Time at which all notes terminate
   * @return An optional musical element
   */
  def mergeNotes(
    musicalElements: List[MusicalElement],
    endTime: BigInt,
    timeUnit: TimeUnit = NANOSECONDS): Option[MusicalElement] = {
    def computeDuration(em: MusicalElement) = timeUnit.toNanos(endTime.toLong) - em.getStartTimeNS
    musicalElements match {
      case Nil =>
        None
      case element :: Nil =>
        Some(element.withDuration(computeDuration(element)))
      case elements =>
        Some(Chord(elements.map(elem => elem.withDuration(computeDuration(elem)))))
    }
  }

  def mergePhrases(phrase: Phrase): Phrase =
    phrase.polyphony.option(mergePhrases(phrase.musicalElements.asInstanceOf[List[Phrase]])).getOrElse(phrase)

  def mergePhrases(phrases: Traversable[Phrase]): Phrase = {
    val notesByStartTime = CollectionUtils.mergeMultiMaps(phrases.toList: _*)(getNotesByStartTimeNS)
    val phraseElements = mutable.MutableList[MusicalElement]()
    var activeNotes: List[MusicalElement] = List()
    val endTimes = notesByStartTime.flatMap { case (startTime, notes) =>
      notes.map(note => startTime + note.getDurationNS)
    }
    val times = notesByStartTime.keySet.toList.++(endTimes).distinct.sorted

    for (time <- times) {
      mergeNotes(activeNotes, time)
        .filter(_.getDuration(TimeUnit.MILLISECONDS) > 1) // Filter our spurious notes
        .foreach(addToPhraseElements(_, phraseElements))

      activeNotes = activeNotes.flatMap(MusicalElement.resizeIfActive(time, _))
      activeNotes ++= notesByStartTime.getOrElse(time, Set()).toList
    }

    require(activeNotes.isEmpty, "All active notes must be consumed")
    Phrase().withMusicalElements(phraseElements)
  }

  def addToPhraseElements(element: MusicalElement, phraseElements: mutable.MutableList[MusicalElement]): Unit = {
    if (phraseElements.isEmpty) {
      if (element.getStartTimeNS > 0) {
        phraseElements += new Rest(durationNS = element.getStartTimeNS)
      }
      phraseElements += element
    } else {
      val previousElem = phraseElements.last
      (previousElem, element) match {
        case (previousNote: Note, note: Note) if Note.areEqual(note, previousNote, duration = false) =>
          phraseElements.updateLast(previousNote.withDuration(previousNote.durationNS + note.durationNS))
        case (previousRest: Rest, rest: Rest) =>
          phraseElements.updateLast(previousRest.withDuration(previousRest.durationNS + rest.durationNS))
        case _ =>
          if (element.getStartTimeNS > phraseElements.last.getEndTimeNS + 1) {
            phraseElements += new Rest(startTimeNS = phraseElements.last.getEndTimeNS)
              .withEndTime(element.getStartTimeNS - 1)
          }
          phraseElements += element
      }
    }
  }


  def split(phrase: Phrase, splitTimeNS: BigInt): (Option[Phrase], Option[Phrase]) = phrase match {
    case p@Phrase(_, true, _, _) =>
      splitPolyphonic(p, splitTimeNS)
    case p@Phrase(_, false, _, _) =>
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

  /**
   * If it is a polyphonic phrase, it returns the sub-phrase with more elements. Otherwise, it returns the provided
   * phrase
   * @param phrase
   */
  def getLongestSubPhrase(phrase: Phrase, constraints: List[Phrase => Boolean]) = {
    if (phrase.polyphony)
      phrase.musicalElements.asInstanceOf[List[Phrase]].filter(constraints).maxBy(_.size)
    else
      phrase
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
  tempoBPM: Double = MusicalElement.DEFAULT_TEMPO_BPM,
  keySignature: KeySignature = Phrase.DEFAULT_KEY_SIGNATURE)
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
