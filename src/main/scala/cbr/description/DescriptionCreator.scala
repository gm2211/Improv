package cbr.description

import cbr.MusicalCase
import cbr.description.features.Feature
import cbr.description.features.extractors.WeightedFeatureExtractor
import com.fasterxml.jackson.annotation.JsonTypeInfo
import instruments.InstrumentType.InstrumentType
import representation.Phrase

object PhraseDescriptionCreators extends DescriptionCreatorFactory[MusicalCase] {
  def getDefault: DescriptionCreator[MusicalCase] = new DefaultPhraseDescriptionCreator
  override def make = getDefault
}

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
trait DescriptionCreator[Element] {
  val featureExtractor: WeightedFeatureExtractor[Element]

  /**
   * Returns the maximum of CaseDescription(s) that can be created by this creator
   * @return Max description size
   */
  def getDescriptionSize: Int = featureExtractor.totalFeaturesSize

  /**
   * Creates a description for the provided element by using the feature extractor to extract features from the case
   * @param elem Element for which a description should be created
   * @return A case description
   */
  def createCaseDescription(elem: Element): CaseDescription[Element] = new CaseDescription[Element] {
    override val weightedFeatures: List[(Double, Feature[Element])] = featureExtractor.extractFeatures(elem)
  }
}
