package training

import cbr.{CaseIndex, MusicalCase}
import storage.Saveable

class SystemTrainer(
  val extractor: CaseExtractor[MusicalCase] = MusicalCaseExtractors.getDefault(),
  val index: CaseIndex[MusicalCase] with Saveable) {

  def addCasesToIndex(filenames: List[String]): Unit = {
    filenames.foreach(addCasesToIndex)
    index.save()
  }

  def addCasesToIndex(filename: String): Unit = {
    val cases = extractor.getCases(filename)
    cases.foreach { case (desc, sol) => index.addSolutionToProblem(desc, sol) }
  }
}
