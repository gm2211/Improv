package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase
import utils.NumericUtils

class VarianceOfStartTimeIntervals extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val startTimeIntervals = Phrase.computeStartTimeIntervals(phrase)
    Feature.from(NumericUtils.variance(startTimeIntervals))
  }

  override val featureSize: Int = 1
}
