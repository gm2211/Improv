package demos

import cbr.description.PhraseDescriptionCreators
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils
import utils.{IOUtils, SerialisationUtils}

import scala.util.Try

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.loadOrCreateDefault[Phrase](PhraseDescriptionCreators.getDefault)
    val cases = TrainingUtils.getCasesFromMIDI(filename)
    cases.foreach{ case (problem, solution) => index.addSolutionToProblem(problem, solution)}
    index.save()
  }
}
