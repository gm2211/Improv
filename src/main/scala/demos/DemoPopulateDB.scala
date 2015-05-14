package demos

import cbr.{CaseDescription, Feature}
import representation.{Note, Phrase}
import storage.KDTreeIndex
import utils.IOUtils

import scala.util.Random

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.loadOrCreate[CaseDescription, Phrase](IOUtils.getResourcePath("knowledgeBase/caseIndex"))
    val dummyD = new CaseDescription {
      override def getSignature: Array[Double] = Array.fill[Double](10)(Random.nextDouble())
      override val weightedFeatures: List[(Double, Feature)] = List((10.0, new Feature {}))
    }
//    println(index.removeCase(dummyD))
//    println(index.addCase(dummyD, Phrase().withMusicalElements(Note())))
    println(index.findKNearestNeighbours(dummyD, 3))
    println(index.save())
  }
}
