package storage

import cbr.description.{CaseDescription, DescriptionCreator}
import cbr.{CaseIndex, CaseSolutionStore}
import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import net.sf.javaml.core.kdtree.KDTree
import utils.{IOUtils, SerialisationUtils}

import scala.collection.JavaConversions._
import scala.language.reflectiveCalls
import scala.math
import scala.util.Try

object KDTreeIndex {
  private val DEFAULT_KDTREE_REBALANCING_THRESHOLD = 1000
  private val DEFAULT_INDEX_RESOURCE: String = "knowledgeBase/caseIndex"
  private val DEFAULT_SOLUTION_STORE_RESOURCE: String = "knowledgeBase/solutionStore"

  def loadOrCreateDefault[Case : Manifest](
      descriptionCreator: DescriptionCreator[Case]): KDTreeIndex[Case] = {
    val loaded = load[Case](DEFAULT_INDEX_RESOURCE)
    loaded.getOrElse(createDefault(descriptionCreator))
  }

  def loadDefault[Case : Manifest]: Option[KDTreeIndex[Case]] = load(DEFAULT_INDEX_RESOURCE)

  def load[Case : Manifest](filename: String): Option[KDTreeIndex[Case]] =
    SerialisationUtils.deserialise[KDTreeIndex[Case]](IOUtils.getResourcePath(filename)).toOption

  def createDefault[Case : Manifest](descriptionCreator: DescriptionCreator[Case]): KDTreeIndex[Case] = {
    removeDefault()
    val store = new MapDBSolutionStore[Case](IOUtils.getResourcePath(DEFAULT_SOLUTION_STORE_RESOURCE))
    new KDTreeIndex[Case](store, descriptionCreator, IOUtils.getResourcePath(DEFAULT_INDEX_RESOURCE))
  }

  def loadOrCreate[Case : Manifest](
      filename: String,
      descriptionCreator: DescriptionCreator[Case]): KDTreeIndex[Case] = {
    load(filename).getOrElse(createDefault(descriptionCreator))
  }

  private def removeDefault(): Unit = {
    IOUtils.deleteContent(IOUtils.getResourcePath(DEFAULT_INDEX_RESOURCE))
    IOUtils.deleteContent(IOUtils.getResourcePath(DEFAULT_SOLUTION_STORE_RESOURCE))
  }
}


@JsonCreator
class KDTreeIndex[Problem] (
      @JsonProperty("store") private val store: CaseSolutionStore[Problem],
      @JsonProperty("descriptionCreator") override val descriptionCreator: DescriptionCreator[Problem],
      @JsonProperty("path") private var path: String
    )  extends CaseIndex[Problem] with Saveable {
  @JsonProperty("kdTree")
  private val kdTree = {
    new KDTree[String](
      descriptionCreator.getDescriptionSize,
      KDTreeIndex.DEFAULT_KDTREE_REBALANCING_THRESHOLD)
  }

 override def addSolutionToProblem(problemDescription: CaseDescription[Problem], solution: Solution): Unit = {
    // Avoiding 'zombie' solutions since KDTree does not support multiple keys
    removeSolutionToProblem(problemDescription)
    val storedSolutionID = store.addSolution(solution)
    kdTree.insert(problemDescription.getSignature, storedSolutionID)
  }

  override def findSolutionsToSimilarProblems(caseDescription: CaseDescription[Problem], k: Int): List[Solution] = {
    val maxNumOfNeighbours = math.min(k, kdTree.getNodeCount)
    Try(kdTree.nearest(caseDescription.getSignature, maxNumOfNeighbours).toList)
      .toOption
      .map(_.flatMap(store.getSolution)).getOrElse(List())
  }


  override def removeSolutionToProblem(caseDescription: CaseDescription[Problem]): Boolean = {
    val key: Array[Double] = caseDescription.getSignature
    removeEntry(key)
  }

  private def removeEntry(key: Array[Double]): Boolean = {
    Try(kdTree.search(key)).map { solutionID =>
      store.removeSolution(solutionID)
      kdTree.delete(key)
    }.isSuccess
  }

  override def clear(): KDTreeIndex.this.type = {
    this.foreach(node => removeEntry(node.getSignature))
    store.clear()
    this
  }

  /**
   * Compacts the index potentially removing entries marked for removal
   *
   * Note: My modified version of KDTree will already re-balance itself every time
   * a certain threshold of nodes marked as deleted
   */
  def compact(): KDTreeIndex.this.type = {
    kdTree.rebalance()
    this
  }

  override def foreach[U](f: (CaseDescription[Problem]) => U): Unit = {
    kdTree.withFilter(!_.isDeleted).foreach( node => f(node.getKey.getCoord))
  }

  override def save(path: Option[String] = None): Try[Boolean] = {
    store.commit()
    val filePath = path.getOrElse(this.path)
    this.path = filePath
    SerialisationUtils.serialise(this, filePath)
  }
}
