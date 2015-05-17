package utils

import scala.math.BigDecimal.RoundingMode

object NumericUtils {
  def round(value: BigDecimal, digits: Int): BigDecimal =
    value.setScale(digits, RoundingMode.HALF_EVEN)
}
