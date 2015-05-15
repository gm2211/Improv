package cbr

import scala.language.implicitConversions

object CaseDescription {
  implicit def fromArray(array: Array[Double]): CaseDescription = new CaseDescription {
    override def getSignature: Array[Double] = array
    override val weightedFeatures: List[(Double, Feature)] = List()
  }
}

trait CaseDescription {
  def getSignature: Array[Double]
  val weightedFeatures: List[(Double, Feature)]
}
