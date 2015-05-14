package training

import cbr.{Feature, CaseDescription}
import representation.Phrase

class MusicalDescriptionCreator extends DescriptionCreator[Phrase] {
  override def createCaseDescription(phrase: Phrase): CaseDescription = {
    new CaseDescription {
      override val weightedFeatures: List[(Double, Feature)] = List()
      override def getSignature: Array[Double] = Array[Double](0)
    }
  }
}