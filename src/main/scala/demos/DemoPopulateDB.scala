package demos

import cbr.CaseDescription
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils
import utils.IOUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.loadOrCreate[CaseDescription, Phrase](IOUtils.getResourcePath("knowledgeBase/caseIndex"))
//    TrainingUtils.addCasesToIndex(index, filename)
    println(TrainingUtils.getCasesFromMIDI(filename))
    index.save()
  }
}
