package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.Phrase

import scala.util.Try

class MaximumDurationExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val duration: Double = Try {
      phrase.maxBy(_.getDurationBPM(phrase.tempoBPM))
        .getDurationBPM(phrase.tempoBPM)
        .toDouble
    }.getOrElse(0)
    Feature.from(duration)
  }

  override val featureSize: Int = 1
}
