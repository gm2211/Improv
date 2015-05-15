package utils.collections

import scala.collection.mutable
import scala.util.Try

class EnhancedTraversable[A](val traversable: TraversableOnce[A]) {
  def groupByMultiMap[K](f: A => K): mutable.MultiMap[K, A] = {
    traversable.foldLeft(CollectionUtils.createHashMultimap[K, A]) { case (mmap, elem) =>
      mmap.addBinding(f(elem), elem)
    }
  }

  def pairsToMultiMap[K : Manifest, V : Manifest](implicit evidence: A =:= (K, V)): mutable.MultiMap[K, V] = {
    traversable.foldLeft(CollectionUtils.createHashMultimap[K, V]) { case (mmap, (key: K, value: V)) =>
      mmap.addBinding(key, value)
    }
  }

  def numericFold[K](defaultValue: K, fn: A => K)(implicit num: Numeric[K]): K =
    traversable.foldLeft[K](defaultValue) { case (acc, elem) => num.plus(acc, fn(elem)) }

  def zipped[K](implicit evidence: A => TraversableOnce[K]): Stream[TraversableOnce[Option[K]]] = {
    val elemSize = traversable.maxBy{ case e => e.size }.size
    val iterators = traversable.map(_.toIterator).toList
    (0 until elemSize).toStream.map{ case i =>
      iterators.map(iter => Try{ iter.next() }.toOption)
    }
  }

  def zipped[K](defaultValue: K)(implicit evidence: A => TraversableOnce[K]): Stream[TraversableOnce[K]] = {
    val elemSize = traversable.maxBy{ case e => e.size }.size
    val iterators = traversable.map(_.toIterator).toList
    (0 until elemSize).toStream.map{ case i =>
      iterators.map(iter => Try{ iter.next() }.getOrElse(defaultValue))
    }
  }
}
