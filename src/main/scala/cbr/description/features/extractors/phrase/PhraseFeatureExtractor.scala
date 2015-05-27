package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.SingleFeatureExtractor
import com.fasterxml.jackson.annotation.JsonTypeInfo
import representation.Phrase

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
abstract class PhraseFeatureExtractor extends SingleFeatureExtractor[Phrase] {
  override def extractFeature(phrase: Phrase): Feature[Phrase] = {
    if (phrase.polyphony) {
      val nonPolyphonicPhrases = phrase.musicalElements.asInstanceOf[List[Phrase]]
      val durations = phrase.map(_.getDurationBPM(phrase.tempoBPM).toDouble)
      val totalDuration = durations.sum
      val normalisedWeights = durations.map(_ / totalDuration).toList
      combine(normalisedWeights, nonPolyphonicPhrases.map(extractFeatureFromNonPolyphonic))
    } else {
      extractFeatureFromNonPolyphonic(phrase)
    }
  }

  private def combine(weights: List[Double], features: List[Feature[Phrase]]): Feature[Phrase] = {
    require(features.forall(_.size == features.head.size))
    val values = Array.fill[Double](features.head.size)(0)
    for (
          (feature, featIdx) <- features.zipWithIndex;
          (value, idx) <- feature.getSignature.zipWithIndex ) {
//      values(idx) = value * weights(featIdx)
      values(idx) = scala.math.max(values(idx), value)
    }
    Feature.from(values)
  }

  def extractFeatureFromNonPolyphonic(phrase: Phrase): Feature[Phrase]
}
