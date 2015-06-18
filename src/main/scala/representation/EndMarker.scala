package representation

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.TimeUnit

case class EndMarker(startTimeNS: BigInt) extends MusicalElement {
  override def getDuration(timeUnit: TimeUnit): BigInt = 0

  override def getStartTime(timeUnit: TimeUnit): BigInt =
    timeUnit.convert(startTimeNS.toLong, TimeUnit.NANOSECONDS)

  override def withDuration(duration: BigInt, timeUnit: TimeUnit): MusicalElement =
    this

  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): MusicalElement =
    copy(startTimeNS = timeUnit.toNanos(startTime.toLong))
}
