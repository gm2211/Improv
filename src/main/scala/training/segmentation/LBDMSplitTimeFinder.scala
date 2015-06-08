package training.segmentation

import representation.{Note, Phrase}
import utils.ImplicitConversions.toEnhancedIterable
import utils.NumericUtils

import scala.collection.mutable.ListBuffer

object LBDMSplitTimeFinder {
  val DEFAULT_PITCH_WEIGHT = 0.25
  val DEFAULT_IOI_WEIGHT = 0.5
  val DEFAULT_RESTS_WEIGHT = 0.25
}

class LBDMSplitTimeFinder extends SplitTimesFinder {
  private val pitchWeight = LBDMSplitTimeFinder.DEFAULT_PITCH_WEIGHT
  private val ioiWeight = LBDMSplitTimeFinder.DEFAULT_IOI_WEIGHT
  private val restsWeight = LBDMSplitTimeFinder.DEFAULT_RESTS_WEIGHT

  override def getSplitTimes(phrase: Phrase): Traversable[BigInt] = {
    val overallProfile = computeProfile(phrase)
    val peaks = NumericUtils.findPeaks(overallProfile)
    val avg = NumericUtils.avg(peaks)
    val notes = phrase.filterByType[Note].toList

    peaks.withFilter(_._1 >= avg).map{ case (_, idx) =>
        notes(idx).getEndTimeNS
    }
  }

  private def computeProfile(phrase: Phrase): List[BigDecimal] = {
    val getPitch = (note: Note) => BigInt(note.midiPitch)
    val getStartTime = (note: Note) => note.startTimeNS
    val getEndTime = (note: Note) => note.getEndTimeNS

    val pitchProfile = NumericUtils.normalise(computeProfile(phrase, getPitch, getPitch))
    val ioiProfile = NumericUtils.normalise(computeProfile(phrase, getStartTime, getStartTime))
    val restsProfile = NumericUtils.normalise(computeProfile(phrase, getEndTime, getStartTime))

    for (pitchIOIRests <- List(pitchProfile, ioiProfile, restsProfile).zipped.toList) yield {
       pitchWeight * pitchIOIRests.head.getOrElse(BigDecimal(0)) +
       ioiWeight * pitchIOIRests(1).getOrElse(BigDecimal(0)) +
       restsWeight * pitchIOIRests(2).getOrElse(BigDecimal(0))
    }
  }

  private def computeProfile(
      phrase: Phrase,
      paramFN1: Note => BigInt,
      paramFN2: Note => BigInt): List[BigInt] = {
    var prevNote: Option[Note] = None
    val profile = ListBuffer[BigInt]()

    for (note <- phrase.withTypeFilter[Note]) {
        if (prevNote.isDefined) {
          val interval = paramFN1(prevNote.get) - paramFN2(note.asInstanceOf[Note]))))
          profile += interval
        }
        prevNote = Some(note.asInstanceOf[Note])
    }

    profile.toList
  }
}
