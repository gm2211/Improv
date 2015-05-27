package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.{Note, Phrase}

class BasicPitchHistogramExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] =
    Feature.from(Phrase.computePitchHistogram(phrase))

override val featureSize: Int = Note.MAX_MIDI_PITCH + 1 // 0 to MAX_MIDI_PITCH
}
