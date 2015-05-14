package demos

import cbr.CaseDescription
import representation.Phrase
import storage.{KDTreeIndex, MapDBSolutionStore}

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val store = new MapDBSolutionStore[Phrase]("myScoreDB")
    val index = new KDTreeIndex[CaseDescription, Phrase](store, 10)
//    TrainingUtils.addCasesToIndex(index, filename)
  }
}
