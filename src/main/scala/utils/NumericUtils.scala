package utils

import scala.math.BigDecimal.RoundingMode

object NumericUtils {
  def max(num1: BigInt, num2: BigInt): BigInt = if (num1 > num2) num1 else num2
  def min(num1: BigInt, num2: BigInt): BigInt = if (num1 < num2) num1 else num2

  def round(value: BigDecimal, digits: Int): BigDecimal =
    value.setScale(digits, RoundingMode.HALF_EVEN)

  def avg[K](traversable: Traversable[K])(implicit ev: Numeric[K]) = {
    ev.toDouble(traversable.sum) / traversable.size
  }

  def variance[K](traversable: Traversable[K])(implicit num: Numeric[K]): Double = {
    val traversableAvg = avg(traversable)

    traversable.foldLeft(0.0){ (acc, elem) =>
      val distanceFromAvg = num.toDouble(elem) - traversableAvg
      acc + (scala.math.pow(distanceFromAvg, 2) / traversable.size)
    }
  }

  def stdDev[K](traversable: Traversable[K])(implicit num: Numeric[K]): Double = scala.math.sqrt(variance(traversable))

  def normalise[K](numbers: Iterable[K])(implicit ev: Numeric[K]): Iterable[BigDecimal] = {
    val total: BigDecimal = numbers.foldLeft(BigDecimal(0))((acc, num) => acc + ev.toDouble(num))
    numbers.map(n => BigDecimal(ev.toDouble(n)) / total)
  }

  def normalise(numbers: Iterable[BigInt]): Iterable[BigDecimal] = {
    val total = BigDecimal(numbers.sum)
    numbers.map(n => BigDecimal(n) / total)
  }

  /**
   * Returns a list of tuples of peaks and their relative index. A peak is defined as a value such both its predecessor 
   * and its successor are strictly less than it
   * @param numbers Series for which peaks should be found
   * @return
   */
  def findPeaks(numbers: List[BigDecimal]): List[(BigDecimal, Int)] = {
    for (idx <- 1 until numbers.size - 1
         if numbers(idx - 1) < numbers(idx) &&
            numbers(idx) > numbers(idx + 1)) yield {
      (numbers(idx), idx)
    }
  }
}
