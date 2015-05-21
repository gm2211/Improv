package cbr.description.features

import cbr.Feature

trait FeatureExtractor[A] {
  def extractFeatures(elem: A): List[(Double, Feature)]
}
