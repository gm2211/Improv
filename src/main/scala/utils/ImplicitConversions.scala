package utils

import utils.collections.{EnhancedTraversable, FasterMutableListWrapper}
import com.google.common.base`

import scala.collection.mutable
import scala.language.implicitConversions

object ImplicitConversions {
  import collection.JavaConversions._
  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }

  implicit def wrapInOption[A <: Any](a: A): Option[A] = Option(a)

  implicit def toFasterMutableList[A](mutableList: mutable.MutableList[A]): FasterMutableListWrapper[A] = new FasterMutableListWrapper[A](mutableList)

  implicit def fromEnhancedTraversable[A](enhancedTraversable: EnhancedTraversable[A]): TraversableOnce[A] =
    enhancedTraversable.traversable

  implicit def toEnhancedTraversable[A](list: java.util.List[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](list)

  implicit def toEnhancedTraversable[A](traversable: Traversable[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](traversable)

  implicit def toEnhancedTraversable[A](traversable: TraversableOnce[A]): EnhancedTraversable[A] =
    new EnhancedTraversable[A](traversable)

  implicit def toDouble(bigDecimal: BigDecimal): Double = bigDecimal.toDouble

  implicit def toLong(bigInt: BigInt): Long = bigInt.toLong

  implicit def toGuavaFunction[From, To](fn: From => To): base.Function[From, To] =
    new base.Function[From, To] {
      override def apply(input: From): To = fn(input)
    }
}
