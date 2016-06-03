package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.{Note, Phrase}

import scala.util.Try

class MaximumPitchExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val notePitches = phrase
      .collect{ case n: Note => n.midiPitch }
    val maxPitch = Try(notePitches.max).getOrElse(0)
    Feature.from(maxPitch)
  }

  override val featureSize: Int = 1
}
