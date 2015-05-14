package demos

import cbr.{CaseDescription, Feature}
import representation.Phrase
import storage.{KDTreeIndex, MapDBSolutionStore}
import utils.{IOUtils, SerialisationUtils}

import scala.util.Try

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val store = new MapDBSolutionStore[Phrase](IOUtils.getResourcePath("knowledgeBase/solutionStore"))
//    val index = new KDTreeIndex[CaseDescription, Phrase](store, 10, IOUtils.getResourcePath("knowledgeBase/caseIndex"))
    val index = KDTreeIndex.loadFromFile[CaseDescription, Phrase](IOUtils.getResourcePath("knowledgeBase/caseIndex")).get
    val dummyD = new CaseDescription {
  override def getSignature: Array[Double] = Array.fill[Double](10)(1.0)

  override val weightedFeatures: List[(Double, Feature)] = List((10.0, new Feature {}))
}
//    index.addCase(dummyD, Phrase())
    println(index.findKNearestNeighbours(dummyD, 1))
//    val indexx = SerialisationUtils.deserialise(IOUtils.getResourcePath("knowledgeBase/caseIndex"), classOf[KDTreeIndex[CaseDescription, Phrase]])
//    indexx.failed.foreach(println)
    println(index.save())
  }
}
