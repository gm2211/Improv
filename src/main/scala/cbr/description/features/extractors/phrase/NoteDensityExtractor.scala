package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import representation.{Note, Phrase}
import utils.ImplicitConversions.toEnhancedIterable
import utils.NumericUtils

class NoteDensityExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    val noteCount = phrase.countIfMatchesType[Note]
    val density = noteCount / NumericUtils.max(phrase.getDurationBPM(phrase.tempoBPM), 1)
    Feature.from(density.toDouble)
  }

  override val featureSize: Int = 1
}
