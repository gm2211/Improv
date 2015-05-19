package representation

import scala.concurrent.duration.{TimeUnit, NANOSECONDS}

object Rest {
  val DEFAULT_DURATION = 0
  val DEFAULT_START_TIME = 0
}

case class Rest(
    durationNS: BigInt = Rest.DEFAULT_DURATION,
    startTimeMS: BigInt = Rest.DEFAULT_START_TIME) extends MusicalElement {
  override def getDuration(timeUnit: TimeUnit): BigInt =
    timeUnit.convert(durationNS.toLong, NANOSECONDS)

  override def getStartTime(timeUnit: TimeUnit): BigInt =
    timeUnit.convert(startTimeMS.toLong, NANOSECONDS)

  override def withDuration(duration: BigInt, timeUnit: TimeUnit): Rest =
    copy(durationNS = timeUnit.toNanos(duration.toLong))

  override def withStartTime(startTime: BigInt, timeUnit: TimeUnit): Rest =
    copy(startTimeMS = timeUnit.toNanos(startTimeMS.toLong))
}
