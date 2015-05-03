package utils

import scala.language.implicitConversions

object ImplicitConversions {
  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }

  implicit def wrapInOption[A <: Any](a: A): Option[A] = Option(a)
}
