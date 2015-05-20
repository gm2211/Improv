package representation

import utils.NumericUtils

import scala.concurrent.duration.{NANOSECONDS, TimeUnit}
import scalaz.Scalaz._
import scala.math

object MusicalElement {
  val DEFAULT_TEMPO_BPM = 120.0
  private def tempoInBeatsPerNanoSec(tempoBPM: BigDecimal): BigDecimal = (tempoBPM / 60) / 1000000000

  def fromBPM(
      beats: BigDecimal,
      tempoBPM: BigDecimal = DEFAULT_TEMPO_BPM,
      resultTimeUnit: TimeUnit = NANOSECONDS): BigInt = {
    resultTimeUnit.convert((beats / tempoInBeatsPerNanoSec(tempoBPM)).toLong, NANOSECONDS)
  }

  def toBPM(
      time: BigInt,
      tempoBPM: BigDecimal = DEFAULT_TEMPO_BPM,
      timeUnit: TimeUnit = NANOSECONDS): BigDecimal = {
    timeUnit.toNanos(time.toLong) * tempoInBeatsPerNanoSec(tempoBPM)
  }

  def isActive(timeNS: BigInt, elem: MusicalElement): Boolean =
    elem.getStartTimeNS <= timeNS && timeNS < elem.getStartTimeNS + elem.getDurationNS

  def resizeIfActive(newStartTimeNS: BigInt, elem: MusicalElement): Option[MusicalElement] = {
    MusicalElement.isActive(newStartTimeNS, elem).fold(
      fromNewStartTimeToPreviousEndTime(newStartTimeNS, elem),
      None
    )
  }

  def fromNewStartTimeToPreviousEndTime(newStartTimeNS: BigInt, elem: MusicalElement): Option[MusicalElement] = {
    val newDuration: BigInt = elem.getStartTimeNS + elem.getDurationNS - newStartTimeNS
    (newDuration > 0).option {
      elem
        .withStartTime(newStartTimeNS)
        .withDuration(newDuration)
    }
  }

  
  def fromPreviousStartTimeToNewEndTime(newEndTimeNS: BigInt, elem: MusicalElement): Option[MusicalElement] = {
    val newDuration = newEndTimeNS - elem.getStartTimeNS
    (newDuration > 0).option(elem.withDuration(newDuration))
  }

  def split(elem: MusicalElement, timeNS: BigInt): (Option[MusicalElement], Option[MusicalElement]) =
    (fromPreviousStartTimeToNewEndTime(timeNS, elem), fromNewStartTimeToPreviousEndTime(timeNS, elem))
}

trait MusicalElement {
  def withStartTime(startTime: BigInt, timeUnit: TimeUnit = NANOSECONDS): MusicalElement

  def withStartTimeBPM(startTimeBPM: BigDecimal, tempoBPM: Double): MusicalElement =
    withStartTime(MusicalElement.fromBPM(startTimeBPM, tempoBPM))

  def withDuration(duration: BigInt, timeUnit: TimeUnit = NANOSECONDS): MusicalElement

  def withDurationBPM(durationBPM: BigDecimal, tempoBPM: Double): MusicalElement =
    withDuration(MusicalElement.fromBPM(durationBPM, tempoBPM))

  def getDurationBPM(tempoBPM: BigDecimal): BigDecimal = MusicalElement.toBPM(getDurationNS, tempoBPM = tempoBPM)

  def getDurationNS: BigInt = getDuration(NANOSECONDS)

  def getDuration(timeUnit: TimeUnit): BigInt

  def getStartTimeBPM(tempoBPM: BigDecimal): BigDecimal = MusicalElement.toBPM(getStartTimeNS, tempoBPM = tempoBPM)

  def getStartTimeNS: BigInt = getStartTime(NANOSECONDS)

  def getStartTime(timeUnit: TimeUnit): BigInt
}
