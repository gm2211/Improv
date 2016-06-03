package utils

import com.google.common.base
import com.google.common.base.Predicate
import utils.collections.{EnhancedIterable, FasterMutableListWrapper}

import scala.collection.mutable
import scala.language.implicitConversions
import scala.util.Try

object ImplicitConversions {
  import collection.JavaConversions._
  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }

  implicit def wrapInOption[A <: Any](a: A): Option[A] = Option(a)

  implicit def toFasterMutableList[A](mutableList: mutable.MutableList[A]): FasterMutableListWrapper[A] = new FasterMutableListWrapper[A](mutableList)

  implicit def fromEnhancedIterable[A](enhancedTraversable: EnhancedIterable[A]): TraversableOnce[A] =
    enhancedTraversable.iterable

  implicit def toEnhancedIterable[A](list: java.util.List[A]): EnhancedIterable[A] =
    new EnhancedIterable[A](list)

  implicit def toEnhancedIterable[A](traversable: Traversable[A]): EnhancedIterable[A] =
    new EnhancedIterable[A](traversable.toIterable)

  implicit def toEnhancedIterable[A](traversable: TraversableOnce[A]): EnhancedIterable[A] =
    new EnhancedIterable[A](traversable.toIterable)

  implicit def toDouble(bigDecimal: BigDecimal): Double = bigDecimal.toDouble

  implicit def toLong(bigInt: BigInt): Long = bigInt.toLong

  implicit def toGuavaFunction[From, To](fn: From => To): base.Function[From, To] =
    new base.Function[From, To] {
      override def apply(input: From): To = fn(input)
    }

  implicit def toGuavaPredicate[From](fn: From => Boolean): base.Predicate[From] =
    new Predicate[From] {
      override def apply(input: From): Boolean = fn(input)
    }

  implicit class NotNullOption[T](val t: Try[T]) extends AnyVal {
    def toNotNullOption = t.toOption.flatMap{Option(_)}
  }
}
