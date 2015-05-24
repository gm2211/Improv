package cbr.description.features

import com.fasterxml.jackson.annotation.JsonTypeInfo

object FeatureExtractor {
  def getDefaultForPhrase = JSymbolicPhraseFeatureExtractor.getDefault
}

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
trait FeatureExtractor[Element] {
  def totalFeaturesSize: Int
  def extractFeatures(elem: Element): List[(Double, Feature[Element])]
}
