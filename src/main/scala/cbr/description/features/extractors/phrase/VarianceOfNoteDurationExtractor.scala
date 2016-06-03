package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase
import utils.NumericUtils

class VarianceOfNoteDurationExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val durationVariance = NumericUtils.variance(phrase.map(_.getDurationBPM(phrase.tempoBPM)))
    Feature.from(durationVariance)
  }

  override val featureSize: Int = 1
}
