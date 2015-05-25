package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase
import utils.NumericUtils

class VarianceOfNoteDurationExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val durationVariance = NumericUtils.variance(phrase.map(_.getDurationBPM(phrase.tempoBPM)))
    Feature.from(durationVariance)
  }

  override val featureSize: Int = 1
}
