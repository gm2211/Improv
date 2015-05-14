package storage

import cbr.{CaseDescription, CaseIndex, CaseSolutionStore}
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import net.sf.javaml.core.kdtree.KDTree
import utils.SerialisationUtils

import scala.language.reflectiveCalls

object KDTreeIndex {
  def loadFromFile[CD <: CaseDescription : Manifest, CS : Manifest](filename: String): Option[KDTreeIndex[CD, CS]] =
    SerialisationUtils.deserialise[KDTreeIndex[CD, CS]](filename).toOption
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

