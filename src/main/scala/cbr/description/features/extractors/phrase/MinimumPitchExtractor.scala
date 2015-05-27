package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.{Note, Phrase}

import scala.util.Try

class MinimumPitchExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val notePitches = phrase
      .collect{ case n: Note => n.midiPitch }
    val minPitch = Try(notePitches.min).getOrElse(0)
    Feature.from(minPitch)
  }

  override val featureSize: Int = 1
}
