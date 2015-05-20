package midi.segmentation

import representation.Phrase

trait PhraseSegmenter {
  def split(phrase: Phrase): List[Phrase]
}
