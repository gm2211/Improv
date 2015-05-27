package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase
import utils.collections.CollectionUtils

class MostCommonMelodicIntervalExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val mostCommonInterval = CollectionUtils.mostFrequent(Phrase.computeMelodicIntervals(phrase))
    Feature.from(mostCommonInterval.getOrElse(0.0))
  }

  override val featureSize: Int = 1
}
