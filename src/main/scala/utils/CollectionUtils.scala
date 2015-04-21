package utils

import scala.util.Random

object CollectionUtils {
  def choose[T](collection: Iterable[T]): Option[T] = {
    def selectRandom(iter: Iterable[T]): Option[T] = {
      val idx = Random.nextInt(iter.size - 1)
      iter.view.zipWithIndex.find { case (elem, index) => idx == index }.map(_._1)
    }
    Option(collection).flatMap(selectRandom)
  }

  def randomRange(lowerBound: Int = 1,
                  upperBound: Int = 1000): Range = {
    require(lowerBound >= 0, "Only non-negative bounds are accepted")
    require(upperBound >= 0, "Only non-negative bounds are accepted")

    val lb = math.min(lowerBound, Int.MaxValue)
    val ub = math.min(upperBound, Int.MaxValue)

    require(ub >= lb, "Cannot have a range with upper-bound smaller than lower-bound")
    lb to ub
  }
}
