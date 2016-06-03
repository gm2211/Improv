package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.{Note, Phrase}
import utils.ImplicitConversions.toEnhancedIterable

class RepeatedNotesExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    var prevNote: Option[Note] = None
    var repeatedCount = 0
    val notes: Iterable[Note] = phrase.filterByType[Note]
    for (note <- notes) {
      val repeated = prevNote.exists(_.midiPitch == note.midiPitch)
      if (repeated){
        repeatedCount +=1
      }
      prevNote = Some(note)
    }
    Feature.from(repeatedCount / scala.math.max(notes.size, 1))
  }

  override val featureSize: Int = 1
}
