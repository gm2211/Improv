package training
import cbr.CaseDescription

trait DescriptionCreator[A] {

  def createCaseDescription(elem: A): CaseDescription
}
