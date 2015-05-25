package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.{Note, Phrase}

class RepeatedNotesExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val notes = phrase.collect{ case note: Note =>
      (note.octave, note.midiPitch, note.durationNS, note.loudness, note.intonation)
    }.toList
    val distinctNotes = notes.distinct
    Feature.from(notes.size - distinctNotes.size)
  }

  override val featureSize: Int = 1
}
