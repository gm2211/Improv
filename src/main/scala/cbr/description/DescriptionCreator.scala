package cbr.description

import cbr.description.features.{Feature, FeatureExtractor}
import representation.Phrase

object DescriptionCreator {
  def getDefaultPhraseDescriptionCreator: DescriptionCreator[Phrase] = new DescriptionCreator[Phrase] {
    override val featureExtractor: FeatureExtractor[Phrase] = FeatureExtractor.getDefault
  }
}

trait DescriptionCreator[Element] {
  val featureExtractor: FeatureExtractor[Element]

  /**
   * Returns the maximum of CaseDescription(s) that can be created by this creator
   * @return Max description size
   */
  def getDescriptionSize: Int = featureExtractor.maxFeatureSize

  /**
   * Creates a description for the provided element by using the feature extractor to extract features from the case
   * @param elem Element for which a description should be created
   * @return A case description
   */
  def createCaseDescription(elem: Element): CaseDescription[Element] = new CaseDescription[Element] {
    override val weightedFeatures: List[(Double, Feature[Element])] = featureExtractor.extractFeatures(elem)
  }
}