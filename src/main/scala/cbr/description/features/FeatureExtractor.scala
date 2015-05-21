package cbr.description.features

trait FeatureExtractor[Element] {
  def extractFeatures(elem: Element): List[(Double, Feature[Element])]
}
