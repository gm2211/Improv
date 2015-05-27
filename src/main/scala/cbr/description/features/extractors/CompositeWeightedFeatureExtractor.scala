package cbr.description.features.extractors

import cbr.description.features.Feature
import utils.ImplicitConversions.toEnhancedTraversable

class CompositeWeightedFeatureExtractor[Element](protected val featureExtractors: List[SingleFeatureExtractor[Element]])
    extends WeightedFeatureExtractor[Element] {
  override def extractFeatures(elem: Element): List[(Double, Feature[Element])] = {
    val weight = 1.0 / featureExtractors.size //TODO: Play around with weighting
    featureExtractors.map(extractor => (weight, extractor.extractFeature(elem)))
  }

  override def totalFeaturesSize: Int = featureExtractors.sumBy(0, _.featureSize)
}
