package cbr.description.features

import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.{JsonSubTypes, JsonTypeInfo}

object FeatureExtractor {
  def getDefaultForPhrase = JSymbolicPhraseFeatureExtractor.getDefault
}

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
trait FeatureExtractor[Element] {
  def totalFeaturesSize: Int
  def extractFeatures(elem: Element): List[(Double, Feature[Element])]
}
