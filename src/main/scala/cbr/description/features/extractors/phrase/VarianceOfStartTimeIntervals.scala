package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase
import utils.NumericUtils

class VarianceOfStartTimeIntervals extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val startTimeIntervals = Phrase.computeStartTimeIntervals(phrase)
    Feature.from(NumericUtils.variance(startTimeIntervals))
  }

  override val featureSize: Int = 1
}
