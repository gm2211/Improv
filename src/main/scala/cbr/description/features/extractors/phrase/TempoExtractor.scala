package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase

class TempoExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    Feature.from(phrase.tempoBPM)
  }

  override val featureSize: Int = 1
}
