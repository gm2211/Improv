package utils

import scala.math.BigDecimal.RoundingMode

object NumericUtils {
  def max(num1: BigInt, num2: BigInt): BigInt = if (num1 > num2) num1 else num2
  def min(num1: BigInt, num2: BigInt): BigInt = if (num1 < num2) num1 else num2

  def round(value: BigDecimal, digits: Int): BigDecimal =
    value.setScale(digits, RoundingMode.HALF_EVEN)
}
