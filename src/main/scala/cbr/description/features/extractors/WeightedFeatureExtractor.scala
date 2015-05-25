package cbr.description.features.extractors

import cbr.description.features.Feature
import com.fasterxml.jackson.annotation.JsonTypeInfo

object WeightedFeatureExtractor {
}

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
trait WeightedFeatureExtractorj[Element] {
  def totalFeaturesSize: Int
  def extractFeatures(elem: Element): List[(Double, Feature[Element])]
}
