package storage

import cbr.{CaseDescription, CaseIndex, CaseSolutionStore}
import net.sf.javaml.core.kdtree.KDTree
import utils.SerialisationUtils

import scala.language.reflectiveCalls

object KDTreeIndex {
  def loadFromFile[CD <: CaseDescription, CS](filename: String): Option[KDTreeIndex[CD, CS]] =
    SerialisationUtils.deserialise(filename).toOption
}

class KDTreeIndex[CD <: CaseDescription, CaseSolution](
      caseStore: CaseSolutionStore[CaseSolution],
      descriptionSize: Int,
      private var path: String
    ) extends CaseIndex[CD, CaseSolution] with FileSerialisable {
  private val kdTree = new KDTree[String](descriptionSize)
  private val store = caseStore

  def addCase(caseDescription: CD, caseSolution: CaseSolution): Unit = {
    val storedSolutionID = store.addSolution(caseSolution)
    kdTree.insert(caseDescription.getSignature, storedSolutionID)
  }

  override def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[CaseSolution] = {
    kdTree.nearest(caseDescription.getSignature, k).flatMap { solutionID =>
      store.getSolution(solutionID)
    }
  }

  override def save(path: Option[String] = None): Boolean = {
    val filePath = path.getOrElse(this.path)
    this.path = filePath
    SerialisationUtils.serialise(this, filePath).isSuccess
  }
}
