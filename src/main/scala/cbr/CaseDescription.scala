package cbr

trait CaseDescription {
  def getSignature: Array[Double]
  val weightedFeatures: List[(Double, Feature)]
}
