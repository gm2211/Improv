package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase

class AverageStartTimeIntervalExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val startTimeIntervals = Phrase.computeStartTimeIntervals(phrase)
    Feature.from(Array(startTimeIntervals.sum / phrase.size))
  }

  override val featureSize: Int = 1
}
