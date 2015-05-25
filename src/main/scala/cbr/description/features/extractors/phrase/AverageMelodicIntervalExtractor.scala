package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import representation.Phrase

class AverageMelodicIntervalExtractor extends PhraseFeatureExtractor {
  override def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase] = {
    val intervals: List[Double] = Phrase.computeMelodicIntervals(phrase)
    Feature.from[Phrase](Array(intervals.sum / intervals.length))
  }

  override val featureSize: Int = 1
}
