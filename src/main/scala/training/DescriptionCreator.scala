package training
import cbr.CaseDescription
import representation.Phrase

trait DescriptionCreator[A] {

  def createCaseDescription(elem: A): CaseDescription
}
