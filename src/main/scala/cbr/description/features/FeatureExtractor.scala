package cbr.description.features

object FeatureExtractor {
  def getDefault = JSymbolicFeatureExtractor.getDefault
}

trait FeatureExtractor[Element] {
  def maxFeatureSize: Int
  def extractFeatures(elem: Element): List[(Double, Feature[Element])]
}
