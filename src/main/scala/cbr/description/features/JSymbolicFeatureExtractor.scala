package cbr.description.features

import cbr.description.DescriptionCreator
import instruments.JFugueUtils
import jsymbolic.features.{AverageNoteDurationFeature, MIDIFeatureExtractor}
import jsymbolic.processing.MIDIFeatureProcessor
import representation.Phrase

object JSymbolicFeatureExtractor {
  def getDefault = {
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
    new JSymbolicFeatureExtractor(processor)
  }

  private def getDefaultFeatureExtractors: Array[MIDIFeatureExtractor] = {
    Array(new AverageNoteDurationFeature)
  }
}

class JSymbolicFeatureExtractor(private val processor: MIDIFeatureProcessor) extends FeatureExtractor[Phrase] {
  override def extractFeatures(phrase: Phrase): List[(Double, Feature[Phrase])] = {
    val jsFeatures = processor.getFeatures(Array(JFugueUtils.toSequence(phrase)))
    val features = JSymbolicUtils.convertFeatures[Phrase](jsFeatures)
    val weight = 1.0 / features.length
    features.map(f => (weight, f))
  }

  override def maxFeatureSize: Int = ???
}

object JSymbolicUtils {
  def convertFeatures[A](jsFeatures: Array[Array[Array[Double]]]): List[Feature[A]] = {
    List() // TODO: Implement this
  }
}
