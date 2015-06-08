package training.segmentation

import midi.JMusicParserUtils
import representation._
import utils.ImplicitConversions.toEnhancedIterable
import utils.NumericUtils

import scala.collection.mutable.ListBuffer

object LBDMSplitTimeFinder {
  val DEFAULT_PITCH_WEIGHT = 0.45
  val DEFAULT_IOI_WEIGHT = 0.35
  val DEFAULT_RESTS_WEIGHT = 0.2
}

class LBDMSplitTimeFinder(private val mergePolyphonic: Boolean = true) extends SplitTimesFinder {
  private val pitchWeight = LBDMSplitTimeFinder.DEFAULT_PITCH_WEIGHT
  private val ioiWeight = LBDMSplitTimeFinder.DEFAULT_IOI_WEIGHT
  private val restsWeight = LBDMSplitTimeFinder.DEFAULT_RESTS_WEIGHT

  override def getSplitTimes(originalPhrase: Phrase): Traversable[BigInt] = {
    val phrase = processPolyphonic(originalPhrase)
    val overallProfile = computeProfile(phrase)
    val peaks = NumericUtils.findPeaks(overallProfile)
    val avg = NumericUtils.avg(peaks.map(_._1))
    val notes = phrase.filter { case r: Rest => false; case _ => true }.toList

    val splitTimes = peaks.withFilter(_._1 >= avg).map { case (_, idx) =>
      notes(idx).getEndTimeNS
    }

    var prevSplitTime = BigInt(0)
    // TODO: if `mergePolyphonic`, make sure the splitTimes actually correspond to the non-merged phrase
    val relativeSplitTimes = splitTimes.map { splitTime =>
      val relativeSplit = splitTime - prevSplitTime
      prevSplitTime = splitTime
      relativeSplit
    }

    relativeSplitTimes
  }

  def computeProfile(phrase: Phrase): List[BigDecimal] = {
    val phrase_ = processPolyphonic(phrase)
    val pitchProfile = NumericUtils.normalise(computePitchProfile(phrase_))
    val ioiProfile = NumericUtils.normalise(computeIOIProfile(phrase_))
    val restsProfile = NumericUtils.normalise(computeRestsProfile(phrase_))

    for (pitchIOIRests <- List(pitchProfile, ioiProfile, restsProfile).zipped.toList) yield {
      pitchWeight * pitchIOIRests.head.getOrElse(BigDecimal(0)) +
        ioiWeight * pitchIOIRests(1).getOrElse(BigDecimal(0)) +
        restsWeight * pitchIOIRests(2).getOrElse(BigDecimal(0))
    }
  }

  def computePitchProfile(phrase: Phrase): List[BigInt] = {
    val phrase_ = processPolyphonic(phrase)
    val getPitch = (note: Note) => BigInt(note.midiPitch)
    computeProfile(phrase_, _ + _, getPitch, getPitch)
  }

  def computeIOIProfile(phrase: Phrase): List[BigInt] = {
    val phrase_ = processPolyphonic(phrase)
    val getStartTime = (note: Note) => note.startTimeNS
    computeProfile(phrase_, NumericUtils.min, getStartTime, getStartTime)
  }

  def computeRestsProfile(phrase: Phrase): List[BigInt] = {
    val phrase_ = processPolyphonic(phrase)
    val getStartTime = (note: Note) => note.startTimeNS
    val getEndTime = (note: Note) => note.getEndTimeNS
    computeProfile(phrase_, NumericUtils.min, getEndTime, getStartTime)
  }

  private def computeProfile(
      phrase: Phrase,
      combineFN: (BigInt, BigInt) => BigInt,
      paramFN1: Note => BigInt,
      paramFN2: Note => BigInt): List[BigInt] = {
    require(!phrase.polyphony, "Cannot process polyphonic phrases")
    val intervals = computeIntervals(phrase, combineFN, paramFN1, paramFN2)
    computeProfile(intervals)
  }

  def computeIntervals(
      phrase: Phrase,
      combineFN: (BigInt, BigInt) => BigInt,
      paramFN1: Note => BigInt,
      paramFN2: Note => BigInt): List[BigInt] = {

    def extractValue(elem: MusicalElement, fn: Note => BigInt): Option[BigInt] = elem match {
      case note: Note =>
        Some(fn(note) + 1)
      case chord: Chord =>
        Some(chord.notes.foldLeft(BigInt(0))((acc, note) => combineFN(acc, fn(note) + 1)))
      case _ =>
        None
    }

    var prevValue: Option[BigInt] = None
    val intervals = ListBuffer[BigInt]()

    for (elem <- phrase) {
      val (nextPrevValue, curValueOpt) = (extractValue(elem, paramFN1), extractValue(elem, paramFN2))

      nextPrevValue.foreach { nextPrevValue =>
        curValueOpt.foreach { curValue =>
          if (prevValue.isDefined) {
            val interval = (prevValue.get - curValue).abs
            intervals += interval
          }

          prevValue = Some(nextPrevValue)
        }
      }
    }
    intervals.toList
  }

  def computeProfile(intervals: List[BigInt]) = {
    val profile = ListBuffer[BigInt]()
    var prevInterval: Option[BigInt] = None
    var prevDegreeOfChange: Option[BigInt] = None

    intervals.foreach { interval =>
      if (prevInterval.isDefined) {
        var degreeOfChange: BigInt = 0
        val intervalSum = interval + prevInterval.get

        if (intervalSum > 0) {
          degreeOfChange = (interval - prevInterval.get).abs / intervalSum
        }

        if (prevDegreeOfChange.isDefined) {
          profile += interval * (prevDegreeOfChange.get + degreeOfChange)
        }
        prevDegreeOfChange = Some(degreeOfChange)
      }
      prevInterval = Some(interval)
    }

    profile.toList
  }

  private def processPolyphonic(phrase: Phrase): Phrase = {
    if (mergePolyphonic)
      JMusicParserUtils.mergePhrases(phrase).getOrElse(phrase)
    else
      Phrase.getLongestSubPhrase(phrase)
  }
}
