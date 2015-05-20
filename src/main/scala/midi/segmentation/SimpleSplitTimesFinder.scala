package midi.segmentation

import representation.Phrase

class SimpleSplitTimesFinder(private val splitEveryNS: BigInt) extends SplitTimesFinder {
  override def getSplitTimes(phrase: Phrase): Traversable[BigInt] =
    (1 to (phrase.getEndTimeNS / splitEveryNS).toInt).map(i => splitEveryNS)
}
