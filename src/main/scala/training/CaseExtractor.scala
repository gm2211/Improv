package training

/**
 * Extracts cases from a file
 */
trait CaseExtractor[Problem] {
  type Solution = Problem
  def getCases(filename: String): List[(Problem, Solution)]
}
