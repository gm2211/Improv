package utils

import scala.collection.mutable
import scala.util.Random

object CollectionUtils {
  def chooseRandom[T](collection: Iterable[T]): Option[T] = {
    def selectRandom(iter: Iterable[T]): Option[T] = {
      if (iter.size <= 0)
        return None

      val idx = Random.nextInt(iter.size)
      iter.view.zipWithIndex.find { case (elem, index) => idx == index }.map(_._1)
    }
    Option(collection).flatMap(selectRandom)
  }

  def randomRange(lowerBound: Int = 1,
    upperBound: Int = 1000): Range = {
    require(lowerBound >= 0, "Only non-negative bounds are accepted")
    require(upperBound >= 0, "Only non-negative bounds are accepted")
    require(upperBound >= lowerBound, "Cannot have a range with upper-bound smaller than lower-bound")

    val upperbound_ = math.min(upperBound, Int.MaxValue)
    val lb = Random.nextInt(upperbound_)
    val ub = lb + Random.nextInt(upperbound_ - lb)

    lb to ub
  }

  def createHashMultimap[Keys, Values]: mutable.HashMap[Keys, Array[Values]] with mutable.MultiMap[Keys, Values] = {
    new mutable.HashMap[Keys, Array[Values]]() with mutable.MultiMap[Keys, Values]
  }

}
