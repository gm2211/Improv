package tests.testutils

import java.util.concurrent.TimeUnit

object ProfilingUtils {
  /**
   * Measures the average time required to perform a task
   * @param task Task to be timed
   * @param reps Amount of times the task should be performed
   * @param timeUnit The time unit to return the time in (defaults to milliseconds)
   * @tparam A Result of task
   * @return A pair of the time required and the result of the task
   */
  def timeIt[A](task: => A, reps: Int = 1, timeUnit: TimeUnit = TimeUnit.MILLISECONDS): (Long, A) = {
    var result: Option[A] = None
    val avgTime: Long = (1 to reps).map { i =>
      val start = System.nanoTime
      result = Some(task)
      System.nanoTime - start
    }.sum / reps

    (timeUnit.convert(avgTime, TimeUnit.NANOSECONDS), result.get)
  }
}
