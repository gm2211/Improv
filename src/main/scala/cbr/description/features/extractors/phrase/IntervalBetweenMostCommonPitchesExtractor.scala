package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase

class IntervalBetweenMostCommonPitchesExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val pitches = Phrase.computePitchHistogram(phrase)
    val interval = pitches.zipWithIndex.sortBy(_._1).take(2).map(_._2).reduce(_ - _)
    Feature.from(interval)
  }

  override val featureSize: Int = 1
}
