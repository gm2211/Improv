package tests.utils

object OptionUtils {
  def getOrElse[A](default: A)(option: Option[A]) = option.getOrElse(default)

  object Optional {
    def unapply[T](a: T) = if (null == a) Some(None) else Some(Some(a))
  }
}
