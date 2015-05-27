package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.{SingleFeatureExtractor, WeightedFeatureExtractor}
import instruments.InstrumentType.InstrumentType
import representation.Phrase

class CompositePhraseFeatureExtractor(featureExtractors: List[SingleFeatureExtractor[Phrase]])
  extends WeightedFeatureExtractor[(InstrumentType, Phrase)] {
  override def extractFeatures(
      instrumentAndPhrase: (InstrumentType, Phrase)): List[(Double, Feature[(InstrumentType, Phrase)])] = {
    val weight = 1.0 / featureExtractors.size
    val (instrumentType, phrase) = instrumentAndPhrase
    for (extractor <- featureExtractors) yield {
      var feature: Option[Feature[Phrase]] = None
      if (phrase.polyphony) {
        feature = Some(extractFromPolyphonic(phrase, extractor))
      } else {
        feature = Some(extractor.extractFeature(phrase))
      }
      (weight, addInstrumentType(feature.get, instrumentType))
    }
  }

  private def extractFromPolyphonic(
      phrase: Phrase,
      extractor: SingleFeatureExtractor[Phrase]): Feature[Phrase] = {
    val nonPolyphonicPhrases = phrase.musicalElements.asInstanceOf[List[Phrase]]
    val durations = phrase.map(_.getDurationBPM(phrase.tempoBPM).toDouble)
    val totalDuration = durations.sum
    val normalisedWeights = durations.map(_ / totalDuration).toList
    val subPhrasesFeatures = nonPolyphonicPhrases.map(extractor.extractFeature)
    combine(normalisedWeights, subPhrasesFeatures)
  }

  private def combine(
      weights: List[Double],
      features: List[Feature[Phrase]]): Feature[Phrase] = {
    require(features.forall(_.size == features.head.size))
    val values = Array.fill[Double](features.head.size)(0)
    for (
      (feature, featIdx) <- features.zipWithIndex;
      (value, idx) <- feature.getSignature.zipWithIndex) {
      //      values(idx) = value * weights(featIdx) //TODO: Decided whether to use weights or just use the max
      values(idx) = scala.math.max(values(idx), value)
    }
    Feature.from(values)
  }

  private def addInstrumentType(feature: Feature[Phrase], instr: InstrumentType): Feature[(InstrumentType, Phrase)] =
    Feature.from(instr.instrumentNumber.toDouble +: feature.getSignature)

  override def totalFeaturesSize: Int =
    featureExtractors.foldLeft(0)((acc, extractor) => acc + extractor.featureSize + 1)
}
