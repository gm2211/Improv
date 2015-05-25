package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase
import utils.collections.CollectionUtils

class MostCommonMelodicIntervalExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val mostCommonInterval = CollectionUtils.mostFrequent(Phrase.computeMelodicIntervals(phrase))
    Feature.from(mostCommonInterval.getOrElse(0.0))
  }

  override val featureSize: Int = 1
}
