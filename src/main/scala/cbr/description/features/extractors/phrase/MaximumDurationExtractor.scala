package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase

class MaximumDurationExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.maxBy(_.getDurationBPM(phrase.tempoBPM)).getDurationBPM(phrase.tempoBPM).toDouble)

  override val featureSize: Int = 1
}
