package training
import cbr.description.CaseDescription
import representation.Phrase

/**
 * Extracts cases from a file
 */
trait CaseExtractor[Case] {

  def getCases(filename: String): List[(CaseDescription[Case], Case)]
}
