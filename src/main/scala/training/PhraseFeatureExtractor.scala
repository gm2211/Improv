package training

import cbr.Feature
import instruments.JFugueUtils
import jsymbolic.features.{AverageNoteDurationFeature, MIDIFeatureExtractor}
import jsymbolic.processing.MIDIFeatureProcessor
import representation.Phrase

object PhraseFeatureExtractor {
  def getDefaultExtractor = {
    val featureExtractors = getDefaultFeatureExtractors

    val processor = new MIDIFeatureProcessor(
      true, // Do not use windows
      Double.MaxValue, // Window size
      0.0, // Window overlap
      featureExtractors,
      Array.fill(featureExtractors.length)(true), // Features to be saved to file
      false, // Save features for each window
      false,  // Save overall features
      "dummy", // Save path
      "dummy") // Save path for features definitions
    new PhraseFeatureExtractor(processor) with DescriptionCreator[Phrase]
  }

  private def getDefaultFeatureExtractors: Array[MIDIFeatureExtractor] = {
    Array(new AverageNoteDurationFeature)
  }
}

class PhraseFeatureExtractor(private val processor: MIDIFeatureProcessor) extends FeatureExtractor[Phrase] {
  override def extractFeatures(phrase: Phrase): List[(Double, Feature)] = {
    val features = processor.getFeatures(Array(JFugueUtils.toSequence(phrase)))
    List((1.0,  Feature.from(new Array[Double](0))))
  }
}
