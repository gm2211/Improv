package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.KeySignature.{Major, Minor}
import representation.Phrase

class KeyExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val key = phrase.keySignature
    val value = (key.quality match { case Major => 1; case Minor => 2}) * 100
    Feature.from(value + key.sharps * 10 + key.flats)
  }

  override val featureSize: Int = 1
}
