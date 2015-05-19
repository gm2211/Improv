package representation

import scala.concurrent.duration.{NANOSECONDS, TimeUnit}

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

  def getStartTimeBPM(tempoBPM: BigDecimal): BigDecimal = MusicalElement.toBPM(getDurationNS, tempoBPM = tempoBPM)

  def getStartTimeNS: BigInt = getStartTime(NANOSECONDS)

  def getStartTime(timeUnit: TimeUnit): BigInt
}
