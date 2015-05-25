package cbr.description.features.extractors

import cbr.description.features.Feature
import utils.ImplicitConversions.toEnhancedTraversable

class CombinedWeightedFeatureExtractor[Element](featureExtractors: List[SingleFeatureExtractor[Element]]) extends WeightedFeatureExtractor[Element] {
  override def extractFeatures(elem: Element): List[(Double, Feature[Element])] = {
    featureExtractors.flatMap(_.extractFeatures(elem))
  }

  override def totalFeaturesSize: Int = featureExtractors.sumBy(0, _.totalFeaturesSize)
}
