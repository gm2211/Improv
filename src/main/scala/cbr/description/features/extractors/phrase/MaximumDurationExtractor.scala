package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase

class MaximumDurationExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.maxBy(_.getDurationBPM(phrase.tempoBPM)).getDurationBPM(phrase.tempoBPM).toDouble)

  override val featureSize: Int = 1
}
