package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase

class AverageMelodicIntervalExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val intervals: List[Double] = Phrase.computeMelodicIntervals(phrase)
    Feature.from[Phrase](Array(intervals.sum / intervals.length))
  }

  override val featureSize: Int = 1
}
