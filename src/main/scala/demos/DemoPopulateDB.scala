package demos

import cbr.description.PhraseDescriptionCreators
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.loadOrCreateDefault[Phrase](PhraseDescriptionCreators.getDefault)
    val cases = TrainingUtils.getCasesFromMIDI(filename)
    cases.foreach{ case (problem, solution) => index.addSolutionToProblem(problem, solution)}
    val signatures = cases.map{ case (p, _) => index.descriptionCreator.createCaseDescription(p).getSignature }
    val solutions = cases.map{ case (problem, _) => index.findSolutionsToSimilarProblems(problem, 1) }
    assert(solutions.forall(_.size == 1))
    val pairs = cases.map{ case (problem, solution) => (index.findSolutionsToSimilarProblems(problem, 1).head, solution) }
    println(pairs)
    index.save()
  }
}
