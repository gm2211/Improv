package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.{Note, Phrase}

class RepeatedNotesExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val notes = phrase.collect{ case note: Note =>
      (note.octave, note.midiPitch, note.durationNS, note.loudness, note.accidental)
    }.toList
    val distinctNotes = notes.distinct
    Feature.from(notes.size - distinctNotes.size)
  }

  override val featureSize: Int = 1
}
