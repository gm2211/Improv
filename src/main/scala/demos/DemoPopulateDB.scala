package demos

import cbr.CaseDescription
import representation.Phrase
import storage.KDTreeIndex
import utils.IOUtils

object DemoPopulateDB {
  def run(filename: String): Unit = {
    val index = KDTreeIndex.loadOrCreate[CaseDescription, Phrase](IOUtils.getResourcePath("knowledgeBase/caseIndex"))
    println(index.addCase(Array.fill[Double](10)(4.0), Phrase()))
    println(index.findKNearestNeighbours(Array.fill[Double](10)(1.0), 10))
    index.save()
  }
}
