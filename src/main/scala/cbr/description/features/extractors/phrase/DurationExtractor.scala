package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.{MusicalElement, Phrase}

class DurationExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.getDurationBPM(MusicalElement.DEFAULT_TEMPO_BPM).toDouble)

  override val featureSize: Int = 1

}
