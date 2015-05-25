package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.{MusicalElement, Phrase}

class DurationExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] =
    Feature.from(phrase.getDurationBPM(MusicalElement.DEFAULT_TEMPO_BPM).toDouble)

  override val featureSize: Int = 1

}
