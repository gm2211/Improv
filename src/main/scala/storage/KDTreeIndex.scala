package storage

import cbr.{CaseDescription, CaseIndex, CaseSolutionStore}
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import net.sf.javaml.core.kdtree.KDTree
import utils.{IOUtils, SerialisationUtils}

import scala.language.reflectiveCalls
import scala.util.Try

object KDTreeIndex {
  def loadOrCreate[CD <: CaseDescription : Manifest, CS : Manifest](
      filename: String,
      descriptionSize: Int = 10): KDTreeIndex[CD, CS] = {
    SerialisationUtils.deserialise[KDTreeIndex[CD, CS]](filename).toOption.getOrElse{
      val store = new MapDBSolutionStore[CS](IOUtils.getResourcePath("knowledgeBase/solutionStore"))
      new KDTreeIndex[CD, CS](store, descriptionSize, IOUtils.getResourcePath("knowledgeBase/caseIndex"))
    }
  }
}


@JsonCreator
class KDTreeIndex[CD <: CaseDescription, CaseSolution] (
      @JsonProperty("store") private val store: CaseSolutionStore[CaseSolution],
      @JsonProperty("ignored") descriptionSize: Int,
      @JsonProperty("path") private var path: String
    )  extends CaseIndex[CD, CaseSolution] with FileSerialisable {
  @JsonProperty("kdTree")
  private val kdTree = new KDTree[String](descriptionSize)

  def addCase(caseDescription: CD, caseSolution: CaseSolution): Unit = {
    val storedSolutionID = store.addSolution(caseSolution)
    kdTree.insert(caseDescription.getSignature, storedSolutionID)
  }

  override def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[CaseSolution] = {
    Try(kdTree.nearest(caseDescription.getSignature, k).toList)
      .toOption
      .map(_.flatMap(store.getSolution)).getOrElse(List())
  }

  override def save(path: Option[String] = None): Boolean = {
    val filePath = path.getOrElse(this.path)
    this.path = filePath
    SerialisationUtils.serialise(this, filePath).isSuccess
  }

  override def removeCase(caseDescription: CD): Boolean = {
    val key: Array[Double] = caseDescription.getSignature

    Try(kdTree.search(key)).map{ solutionID =>
      store.removeSolution(solutionID)
      kdTree.delete(key)
    }.isSuccess
  }

  //TODO: Make this iterable? Will require changes to javaml's KDTree (low priority)
}

