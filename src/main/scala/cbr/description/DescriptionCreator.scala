package cbr.description

import cbr.description.features.{Feature, FeatureExtractor}

trait DescriptionCreator[Element] {
  this: FeatureExtractor[Element] =>

  def createCaseDescription(elem: Element): CaseDescription[Element] = new CaseDescription[Element] {
    override val weightedFeatures: List[(Double, Feature[Element])] = extractFeatures(elem)
  }
}
