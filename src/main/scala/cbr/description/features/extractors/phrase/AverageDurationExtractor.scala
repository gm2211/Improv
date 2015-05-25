package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase
import utils.ImplicitConversions.toEnhancedTraversable

class AverageDurationExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.sumBy(0.0, _.getDurationBPM(phrase.tempoBPM).toDouble) / phrase.size)

  override val featureSize: Int = 1
}
