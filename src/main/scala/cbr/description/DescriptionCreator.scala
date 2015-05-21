package cbr.description

import cbr.Feature
import cbr.description.features.FeatureExtractor

trait DescriptionCreator[A] {
  this: FeatureExtractor[A] =>

  def createCaseDescription(elem: A): CaseDescription = new CaseDescription {
    override val weightedFeatures: List[(Double, Feature)] = extractFeatures(elem)
  }
}
