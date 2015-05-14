package storage

import cbr.{CaseSolutionStore, CaseDescription, CaseIndex}
import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional
import net.sf.javaml.core.kdtree.KDTree
import utils.builders.Once

import scala.language.reflectiveCalls

//case class KDTreeIndexBuilder[
//  CD <: CaseDescription,
//  CaseSolution,
//  CaseSolStoreCount <: Count,
//  DescriptionSizeCount <: Count](
//    solutionStore: Option[CaseSolutionStore[CaseSolution]] = None,
//    descriptionSize: Option[Int] = None) {
//
//  def withSolutionStore(solutionStore: CaseSolutionStore[CaseSolution]) =
//    copy[CD, CaseSolution, Once, DescriptionSizeCount](solutionStore = Option(solutionStore))
//
//  def withDescriptionSize(descriptionSize: Int) =
//    copy[CD, CaseSolution, CaseSolStoreCount, Once](descriptionSize = Some(descriptionSize))
//
//  def build[
//    A <: CaseSolStoreCount : IsOnce,
//    B <: DescriptionSizeCount : IsOnce]: KDTreeIndex[CD, CaseSolution] =
//      new KDTreeIndex[CD, CaseSolution](this.asInstanceOf[KDTreeIndexBuilder[CD, CaseSolution, Once, Once]])
//}

object KDTreeIndex {
//  def loadFromFile[CD, CS](filename: String): KDTreeIndex[CD, CS] = {
//
//  }
}

class KDTreeIndex[CD <: CaseDescription, CaseSolution](
      caseStore: CaseSolutionStore[CaseSolution],
      descriptionSize: Int
    ) extends CaseIndex[CD, CaseSolution] {
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
}
