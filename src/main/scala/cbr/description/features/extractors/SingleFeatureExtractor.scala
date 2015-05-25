package cbr.description.features.extractors

import cbr.description.features.Feature

trait SingleFeatureExtractor[Element] {
  def extractFeature(elem: Element): Feature[Element]
  def featureSize: Int
}
