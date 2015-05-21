package training
import cbr.description.CaseDescription
import representation.Phrase

/**
 * Extracts cases from a file
 */
trait CaseExtractor[CaseSolution] {

  def getCases(filename: String): List[(CaseDescription, CaseSolution)]
}
