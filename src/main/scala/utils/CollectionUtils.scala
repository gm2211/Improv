package utils

import scala.util.Random

object CollectionUtils {
  def choose[T](collection: Iterable[T]): Option[T] = {
    def selectRandom(iter: Iterable[T]): Option[T] = {
      val idx = Random.nextInt(iter.size)
      return iter.view.zipWithIndex.find{case (elem, index) => idx == index}.map(_._1)
    }
    return Option(collection).flatMap(selectRandom)
  }
}
