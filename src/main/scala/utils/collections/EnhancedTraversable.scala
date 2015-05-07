package utils.collections

import scala.collection.mutable

class EnhancedTraversable[A](private val traversable: TraversableOnce[A]) {
   def groupByMultiMap[K](f: A => K): mutable.MultiMap[K, A] = {
     traversable.foldLeft(CollectionUtils.createHashMultimap[K, A]) { case (mmap, elem) =>
       mmap.addBinding(f(elem), elem)
     }
   }

   def pairsToMultiMap[K, V](implicit evidence: A =:= (K, V)): mutable.MultiMap[K, V] = {
     traversable.foldLeft(CollectionUtils.createHashMultimap[K, V]) { case (mmap, (key: K, value: V)) =>
       mmap.addBinding(key, value)
     }
   }
 }
