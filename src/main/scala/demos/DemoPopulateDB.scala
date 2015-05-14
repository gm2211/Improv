package demos

import cbr.{Feature, CaseDescription}
import representation.Phrase
import storage.{KDTreeIndex, MapDBSolutionStore}
import training.TrainingUtils
import utils.IOUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val store = new MapDBSolutionStore[Phrase](IOUtils.getResourcePath("knowledgeBase/solutionStore"))
//    val index = new KDTreeIndex[CaseDescription, Phrase](store, 10, IOUtils.getResourcePath("knowledgeBase/caseIndex"))
    val index = KDTreeIndex.loadFromFile[CaseDescription, Phrase](IOUtils.getResourcePath("knowledgeBase/caseIndex")).get
    val dummyD = new CaseDescription {
  override def getSignature: Array[Double] = Array.fill[Double](10)(1.0)

  override val weightedFeatures: List[(Double, Feature)] = List((10.0, new Feature {}))
}
    index.addCase(dummyD, Phrase())
    println(index.save())
  }
}
