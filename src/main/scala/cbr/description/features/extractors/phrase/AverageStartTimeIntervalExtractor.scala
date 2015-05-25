package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase

class AverageStartTimeIntervalExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val startTimeIntervals = Phrase.computeStartTimeIntervals(phrase)
    Feature.from(Array(startTimeIntervals.sum / phrase.size))
  }

  override val featureSize: Int = 1
}
