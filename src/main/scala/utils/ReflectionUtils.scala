package utils

import scala.util.Try

object ReflectionUtils {
  def getField[A, K](fieldName: String, obj: A): Option[K] = Try {
    val field = obj.getClass.getDeclaredField(fieldName)
    field.setAccessible(true)
    field.get(obj).asInstanceOf[K]
  }.toOption
}
