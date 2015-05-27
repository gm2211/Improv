package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase
import utils.ImplicitConversions.toEnhancedTraversable

class AverageDurationExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.sumBy(0.0, _.getDurationBPM(phrase.tempoBPM).toDouble) / phrase.size)

  override val featureSize: Int = 1
}
