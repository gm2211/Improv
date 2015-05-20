package midi.segmentation

import representation.Phrase

class SimpleSplitTimesFinder(private val splitEveryNS: BigInt) extends SplitTimesFinder {
  override def getSplitTimes(phrase: Phrase): Traversable[BigInt] =
    splitEveryNS to phrase.getEndTimeNS by splitEveryNS
}
