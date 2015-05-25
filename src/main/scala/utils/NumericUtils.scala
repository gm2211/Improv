package utils

import scala.math.BigDecimal.RoundingMode

object NumericUtils {
  def max(num1: BigInt, num2: BigInt): BigInt = if (num1 > num2) num1 else num2
  def min(num1: BigInt, num2: BigInt): BigInt = if (num1 < num2) num1 else num2

  def round(value: BigDecimal, digits: Int): BigDecimal =
    value.setScale(digits, RoundingMode.HALF_EVEN)

  def variance[K](traversable: Traversable[K])(implicit num: Numeric[K]): Double = {
    val avg = num.toDouble(traversable.sum) / traversable.size
    traversable.foldLeft(0.0)((acc, elem) => acc + scala.math.pow(num.toDouble(elem) - avg, 2)) / traversable.size
  }

  def stdDev[K](traversable: Traversable[K])(implicit num: Numeric[K]): Double = scala.math.sqrt(variance(traversable))
}
