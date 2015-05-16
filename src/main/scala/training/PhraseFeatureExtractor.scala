package training

import cbr.Feature
import representation.Phrase

class PhraseFeatureExtractor extends FeatureExtractor[Phrase]{
  override def extractFeatures(elem: Phrase): List[(Double, Feature)] = List()
}
