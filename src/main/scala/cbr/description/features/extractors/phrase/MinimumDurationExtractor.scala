package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase

class MinimumDurationExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.minBy(_.getDurationBPM(phrase.tempoBPM)).getDurationBPM(phrase.tempoBPM).toDouble)

  override val featureSize: Int = 1

}
