package cbr

import utils.functional.{FunctionalUtils, MemoizedValue}

import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

object CaseDescription {
  implicit def fromArray(array: Array[Double]): CaseDescription = new CaseDescription {
    override def getSignature: Array[Double] = array
    override val weightedFeatures: List[(Double, Feature)] = List()
  }
}

trait CaseDescription {
  val weightedFeatures: List[(Double, Feature)]
  val size: MemoizedValue[Int] = FunctionalUtils.memoized(weightedFeatures.foldLeft(0)(_ + _._2.size))

  /**
   * Returns an array of doubles that represents a multi-dimensional point which corresponds to the case Description
   * @return
   */
  def getSignature: Array[Double] = {
    weightedFeatures.foldLeft(new ArrayBuffer[Double]()) { case (buf, (weight, feature)) =>
        buf.appendAll(feature.getSignature.map(weight * _))
        buf
    }.toArray
  }

}
