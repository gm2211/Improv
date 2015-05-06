package utils

import scala.language.implicitConversions

object ImplicitConversions {
  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }

  implicit def wrapInOption[A <: Any](a: A): Option[A] = Option(a)

  implicit def toEnhancedTraversable[A](traversable: Traversable[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](traversable)

  implicit def toEnhancedTraversable[A](traversable: TraversableOnce[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](traversable)
}
