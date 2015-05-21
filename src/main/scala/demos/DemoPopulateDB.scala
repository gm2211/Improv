package demos

import cbr.description.CaseDescription
import representation.Phrase
import storage.KDTreeIndex
import training.TrainingUtils
import utils.IOUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.getDefault[CaseDescription[Phrase], Phrase]
//    TrainingUtils.addCasesToIndex(index, filename)
    println(TrainingUtils.getCasesFromMIDI(filename))
    index.save()
  }
}
