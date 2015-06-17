import java.util.UUID

import net.sf.javaml.core.kdtree.KDTree
import org.scalatest.FlatSpec
import utils.{NumericUtils, ProfilingUtils}

import scala.collection.mutable.ListBuffer
import scala.util.Random
import collection.JavaConversions._

class KDTreePerformanceAnalysis extends FlatSpec {

  "A KDTree" should "outperform a flat db when performing KNN" in {
    val numOfDimensions: Int = 100
    val numNeighbours: Int = 10

    def genPoint = Array.fill[Double](numOfDimensions)(Random.nextDouble())

    val kdTree = new KDTree[Any](numOfDimensions)

    val kdTimes = ListBuffer[(Long, Long)]()
    val flatTimes = ListBuffer[(Long, Long)]()

    for (i <- 1 to 20) {
      for (j <- 1 to 10000) {
        kdTree.insert(genPoint, null)
      }

      val target = genPoint
      val (kdTime, _) = ProfilingUtils.timeIt(kdTree.nearest(target, numNeighbours))
      val elems = kdTree.toList.map(_.getKey.getCoord)
      val (flatTime, _) = ProfilingUtils.timeIt(flatKNN(elems, target, numNeighbours))

      kdTimes.append((elems.size.toLong, kdTime))
      flatTimes.append((elems.size.toLong, flatTime))

      println(s"${i}th iteration")
    }


    import com.quantifind.charts.Highcharts._
    spline(kdTimes)
    hold()
    spline(flatTimes)
    legend(List("KNN with KDTree", "KNN with flat db"))
    title(s"KNN performance for $numNeighbours neighbours")
    xAxis("# elems")
    yAxis("milliseconds")

    System.in.read()
  }

  def flatKNN(elements: List[Array[Double]], target: Array[Double], k: Int): List[Array[Double]] = {
    def distance = NumericUtils.euclid _
    val sortedElements = elements.sortBy(distance(_, target))
    sortedElements.take(k)

      top(elements.map(elem => (elem, distance(elem, target))).toSet, k)
  }

  def top[A](elems: Set[(A, Double)], k: Int): List[A] = {
    require(k > 0)
    val maxElem = elems.maxBy(_._2)
    var lst = List[A]()

    if (k > 1) {
      lst = top(elems.diff(Set(maxElem)), k - 1)
    }

    lst :+ maxElem._1
  }

}
