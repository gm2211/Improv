package storage

import cbr.{Feature, CaseDescription, CaseIndex, CaseSolutionStore}
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import net.sf.javaml.core.kdtree.KDTree
import utils.{IOUtils, SerialisationUtils}

import scala.language.reflectiveCalls
import scala.util.Try
import collection.JavaConversions._

object KDTreeIndex {
  val DEFAULT_KDTREE_REBALANCING_THRESHOLD = 1000

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
  private val kdTree = new KDTree[String](descriptionSize, KDTreeIndex.DEFAULT_KDTREE_REBALANCING_THRESHOLD)

  def addCase(caseDescription: CD, caseSolution: CaseSolution): Unit = {
    removeCase(caseDescription) // The implementation of KDTree I'm using does not support multiple keys
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

    removeEntry(key)
  }

  override def foreach[U](f: (CaseDescription) => U): Unit = {
    kdTree.withFilter(!_.isDeleted).foreach( node => f(new CaseDescription {
      override def getSignature: Array[Double] = node.getKey.getCoord
      override val weightedFeatures: List[(Double, Feature)] = List()
    }))
  }

  override def clear(): Unit = {
    this.foreach(node => removeEntry(node.getSignature))
    store.clear()
  }

  private def removeEntry(key: Array[Double]): Boolean = {
    Try(kdTree.search(key)).map { solutionID =>
      store.removeSolution(solutionID)
      kdTree.delete(key)
    }.isSuccess
  }
}

