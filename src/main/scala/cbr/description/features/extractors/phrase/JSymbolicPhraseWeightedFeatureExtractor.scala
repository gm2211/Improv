package cbr.description.features.extractors.phrase

import cbr.description.features.Feature
import cbr.description.features.extractors.WeightedFeatureExtractor
import instruments.InstrumentType.PIANO
import instruments.JFugueUtils
import jsymbolic.features._
import jsymbolic.processing.MIDIFeatureProcessor
import representation.Phrase

object JSymbolicPhraseWeightedFeatureExtractor {
  def getDefault = {
    val featureExtractors = getDefaultFeatureExtractors

    val processor = new MIDIFeatureProcessor(
      true, // Do not use windows
      Double.MaxValue, // Window size
      0.0, // Window overlap
      featureExtractors,
      Array.fill(featureExtractors.length)(true.asInstanceOf[java.lang.Boolean]), // Features to be saved to file
      false, // Do not save features for each window
      false, // Do not save overall features
      false, // Do not save to file
      "/dev/null", // Save path
      "/dev/null") // Save path for features definitions
    new JSymbolicPhraseWeightedFeatureExtractor(processor)
  }

  private def getDefaultFeatureExtractors: Array[MIDIFeatureExtractor] = {
    Array(
      new AverageNoteDurationFeature,
      new AverageNoteToNoteDynamicsChangeFeature,
      new AverageMelodicIntervalFeature,
      new AverageTimeBetweenAttacksFeature,
      new BasicPitchHistogramFeature,
      new ChangesOfMeterFeature,
      new ChromaticMotionFeature,
      new CombinedStrengthOfTwoStrongestRhythmicPulsesFeature,
      new DirectionOfMotionFeature,
      new DurationFeature,
      new DistanceBetweenMostCommonMelodicIntervalsFeature,
      new IntervalBetweenStrongestPitchesFeature,
      new MinimumNoteDurationFeature,
      new MostCommonMelodicIntervalFeature,
      new MostCommonMelodicIntervalPrevalenceFeature,
      new MostCommonPitchClassFeature,
      new MostCommonPitchClassPrevalenceFeature,
      new MostCommonPitchFeature,
      new MostCommonPitchPrevalenceFeature,
      new NoteDensityFeature,
      new NumberOfCommonPitchesFeature,
      new RepeatedNotesFeature,
      new VariabilityOfNoteDurationFeature,
      new VariabilityOfTimeBetweenAttacksFeature
    )
  }
}

class JSymbolicPhraseWeightedFeatureExtractor(private val processor: MIDIFeatureProcessor) extends WeightedFeatureExtractor[Phrase] {
  override def extractFeatures(phrase: Phrase): List[(Double, Feature[Phrase])] = {
    val jsFeatures = processor.getFeatures(Array(JFugueUtils.toSequence(phrase, PIANO(1))))
    val features = JSymbolicUtils.convertFeatures[Phrase](jsFeatures)
    val weight = 1.0 / features.length // TODO: Play around with weights?
    features.map(f => (weight, f))
  }

  override def totalFeaturesSize: Int =
    processor.getFeatureExtractors.foldLeft(0)(_ + _.getFeatureDefinition.dimensions)
}

object JSymbolicUtils {
  def convertFeatures[A](jsFeatures: Array[Array[Array[Double]]]): List[Feature[A]] = {
    require(jsFeatures.length == 1)
    for (featureValues <- jsFeatures(0).toList) yield {
      Feature.from[A](featureValues)
    }
  }
}
