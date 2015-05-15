package storage

import cbr.{CaseDescription, CaseIndex, CaseSolutionStore}
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import net.sf.javaml.core.kdtree.KDTree
import utils.{IOUtils, SerialisationUtils}

import scala.collection.JavaConversions._
import scala.language.reflectiveCalls
import scala.math
import scala.util.Try

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
    val maxNumOfNeighbours = math.min(k, kdTree.getNodeCount)
    Try(kdTree.nearest(caseDescription.getSignature, maxNumOfNeighbours).toList)
      .toOption
      .map(_.flatMap(store.getSolution)).getOrElse(List())
  }

  override def save(path: Option[String] = None): Boolean = {
    store.commit()
    val filePath = path.getOrElse(this.path)
    this.path = filePath
    SerialisationUtils.serialise(this, filePath).isSuccess
  }

  override def removeCase(caseDescription: CD): Boolean = {
    val key: Array[Double] = caseDescription.getSignature

    removeEntry(key)
  }

  override def foreach[U](f: (CaseDescription) => U): Unit = {
    kdTree.withFilter(!_.isDeleted).foreach( node => f(node.getKey.getCoord))
  }

  /**
   * Compacts the index potentially removing entries marked for removal
   *
   * Note: My modified version of KDTree will already re-balance itself every time
   * a certain threshold of nodes marked as deleted
   */
  def compact(): Unit = {
    kdTree.rebalance()
  }

  override def clear(): KDTreeIndex.this.type = {
    this.foreach(node => removeEntry(node.getSignature))
    store.clear()
    this
  }

  private def removeEntry(key: Array[Double]): Boolean = {
    Try(kdTree.search(key)).map { solutionID =>
      store.removeSolution(solutionID)
      kdTree.delete(key)
    }.isSuccess
  }
}

