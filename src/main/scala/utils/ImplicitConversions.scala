package utils

import utils.collections.EnhancedTraversable

import scala.collection.convert.{WrapAsScala, WrapAsJava}
import scala.language.implicitConversions

object ImplicitConversions {
  import collection.JavaConversions._
  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }

  implicit def wrapInOption[A <: Any](a: A): Option[A] = Option(a)

  implicit def fromEnhancedTraversable[A](enhancedTraversable: EnhancedTraversable[A]): TraversableOnce[A] =
    enhancedTraversable.traversable

  implicit def toEnhancedTraversable[A](list: java.util.List[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](list)

  implicit def toEnhancedTraversable[A](traversable: Traversable[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](traversable)

  implicit def toEnhancedTraversable[A](traversable: TraversableOnce[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](traversable)
}
