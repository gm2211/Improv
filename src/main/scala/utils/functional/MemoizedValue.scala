package utils.functional

import scala.language.implicitConversions

object MemoizedValue {
  implicit def fromMemoizedValue[T](memValue: MemoizedValue[T]): T = memValue.apply
}

class MemoizedValue[T](private val value: () => T) {
  var storedValue: Option[T] = None

  def apply: T = {
    if (storedValue.isEmpty) {
      storedValue = Some(value())
    }
    storedValue.get
  }
}
