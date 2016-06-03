package cbr.description.features.extractors

import cbr.description.features.Feature
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
trait WeightedFeatureExtractor[Element] {
  def extractFeatures(elem: Element): List[(Double, Feature[Element])]
  def totalFeaturesSize: Int
}
