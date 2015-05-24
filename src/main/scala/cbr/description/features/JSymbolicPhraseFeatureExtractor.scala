package cbr.description.features

import instruments.JFugueUtils
import jsymbolic.features.{AverageNoteDurationFeature, MIDIFeatureExtractor}
import jsymbolic.processing.MIDIFeatureProcessor
import representation.Phrase

object JSymbolicPhraseFeatureExtractor {
  def getDefault = {
    val featureExtractors = getDefaultFeatureExtractors

    val processor = new MIDIFeatureProcessor(
      true, // Do not use windows
      Double.MaxValue, // Window size
      0.0, // Window overlap
      featureExtractors,
      Array.fill(featureExtractors.length)(true.asInstanceOf[java.lang.Boolean]), // Features to be saved to file
      false, // Do not save features for each window
      false,  // Do not save overall features
      false, // Do not save to file
      "/dev/null", // Save path
      "/dev/null") // Save path for features definitions
    new JSymbolicPhraseFeatureExtractor(processor)
  }

  private def getDefaultFeatureExtractors: Array[MIDIFeatureExtractor] = {
    Array(new AverageNoteDurationFeature)
  }
}

class JSymbolicPhraseFeatureExtractor(private val processor: MIDIFeatureProcessor) extends FeatureExtractor[Phrase] {
  override def extractFeatures(phrase: Phrase): List[(Double, Feature[Phrase])] = {
    val jsFeatures = processor.getFeatures(Array(JFugueUtils.toSequence(phrase)))
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
