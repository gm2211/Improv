package training
import cbr.{CaseDescription, Feature}

trait DescriptionCreator[A] {
  this: FeatureExtractor[A] =>

  def createCaseDescription(elem: A): CaseDescription = new CaseDescription {
    override val weightedFeatures: List[(Double, Feature)] = extractFeatures(elem)
  }
}
