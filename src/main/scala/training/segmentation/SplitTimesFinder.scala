package training.segmentation

import representation.Phrase

trait SplitTimesFinder {
  def getSplitTimes(phrase: Phrase): Traversable[BigInt]
}
