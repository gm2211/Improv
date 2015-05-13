package demos

import cbr.{HazelcastStore, CaseDescription, KDTreeIndex}
import representation.Phrase
import training.TrainingUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val store = new HazelcastStore[Phrase]
    val index = new KDTreeIndex[CaseDescription, Phrase](store, 10)
    TrainingUtils.addCasesToIndex(index, filename)
  }
}
