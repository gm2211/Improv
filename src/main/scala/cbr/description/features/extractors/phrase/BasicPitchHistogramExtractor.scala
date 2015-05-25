package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.{Note, Phrase}
import utils.collections.CollectionUtils

class BasicPitchHistogramExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val histogram = Phrase.computePitchHistogram(phrase)
    print(s"histogram($phrase)=> ${CollectionUtils.print(histogram)}")
    Feature.from(histogram)
  }

override val featureSize: Int = Note.MAX_MIDI_PITCH + 1 // 0 to MAX_MIDI_PITCH
}
