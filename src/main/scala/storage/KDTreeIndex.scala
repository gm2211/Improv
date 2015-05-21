package storage

import cbr.description.CaseDescription
import cbr.{CaseIndex, CaseSolutionStore}
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import net.sf.javaml.core.kdtree.KDTree
import representation.Phrase
import utils.{IOUtils, SerialisationUtils}

import scala.collection.JavaConversions._
import scala.language.reflectiveCalls
import scala.math
import scala.util.Try

object KDTreeIndex {
  private val DEFAULT_KDTREE_REBALANCING_THRESHOLD = 1000
  private val DEFAULT_INDEX_RESOURCE: String = "knowledgeBase/caseIndex"
  private val DEFAULT_SOLUTION_STORE_RESOURCE: String = "knowledgeBase/solutionStore"

  def getDefault[CD <: CaseDescription[CS] : Manifest, CS : Manifest]: KDTreeIndex[CD, CS] =
    KDTreeIndex.loadOrCreate(IOUtils.getResourcePath(DEFAULT_INDEX_RESOURCE))

  def loadOrCreate[CD <: CaseDescription[CS] : Manifest, CS : Manifest](
      filename: String,
      descriptionSize: Int = 10): KDTreeIndex[CD, CS] = {
    SerialisationUtils.deserialise[KDTreeIndex[CD, CS]](filename).toOption.getOrElse{
      val store = new MapDBSolutionStore[CS](IOUtils.getResourcePath(DEFAULT_SOLUTION_STORE_RESOURCE))
      new KDTreeIndex[CD, CS](store, descriptionSize, IOUtils.getResourcePath(DEFAULT_INDEX_RESOURCE))
    }
  }
}


@JsonCreator
class KDTreeIndex[CD <: CaseDescription[Case], Case] (
      @JsonProperty("store") private val store: CaseSolutionStore[Case],
      @JsonProperty("ignored") descriptionSize: Int,
      @JsonProperty("path") private var path: String
    )  extends CaseIndex[CD, Case] with FileSerialisable {
  @JsonProperty("kdTree")
  private val kdTree = new KDTree[String](descriptionSize, KDTreeIndex.DEFAULT_KDTREE_REBALANCING_THRESHOLD)

  def addCase(caseDescription: CD, caseSolution: Case): Unit = {
    removeCase(caseDescription) // The implementation of KDTree I'm using does not support multiple keys
    val storedSolutionID = store.addSolution(caseSolution)
    kdTree.insert(caseDescription.getSignature, storedSolutionID)
  }

  override def findKNearestNeighbours(caseDescription: CD, k: Int): Traversable[Case] = {
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

  override def foreach[U](f: (CaseDescription[Case]) => U): Unit = {
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

