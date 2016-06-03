package cbr.description.features.extractors

import cbr.description.features.Feature
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
trait SingleFeatureExtractor[Element] {
  def extractFeature(elem: Element): Feature[Element]
  val featureSize: Int
}
