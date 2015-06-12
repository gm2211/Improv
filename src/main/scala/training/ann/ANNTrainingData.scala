package training.ann

import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}

import org.neuroph.core.data.DataSet
import storage.MapDBMapStore
import utils.collections.CollectionUtils
import utils.{IOUtils, NumericUtils}

import scala.collection.mutable
import scala.language.implicitConversions
import scala.util.Try

object ANNTrainingData {
  val DEFAULT_DB_PATH: String = IOUtils.getResourcePath("annTrainingSet")

  def merge(dataSets: List[ANNTrainingData]): ANNTrainingData = {
    require(dataSets.nonEmpty)
    require(dataSets.forall(dataSets.head.compatibleWith))

    val newDataSet = dataSets.head.copy
    dataSets.tail.foreach(newDataSet.addDataPoints)
    newDataSet
  }

  def loadDefaultDB = {
    MapDBMapStore.loadFromFile[String, ANNTrainingData](DEFAULT_DB_PATH)
  }

  implicit def toNeurophDataset(aNNTrainingData: ANNTrainingData): DataSet = {
    toNeurophDataset(aNNTrainingData, None)
  }

  def toNeurophDataset(
      aNNTrainingData: ANNTrainingData,
      columnNames: Option[Array[String]] = None): DataSet = {
    val dataSet = new DataSet(aNNTrainingData.inputDimensions, aNNTrainingData.outputDimensions)
    aNNTrainingData.getDataPoints.foreach(inOut => dataSet.addRow(inOut._1, inOut._2))

    val columns = columnNames getOrElse (1 to aNNTrainingData.inputDimensions).map(i => s"input$i").toArray[String] ++
        (1 to aNNTrainingData.outputDimensions).map(i => s"output$i").toArray[String]

    dataSet.setColumnNames(columns)
    dataSet
  }
}

class ANNTrainingData(
    val inputDimensions: Int,
    val outputDimensions: Int) extends Serializable {
  def dataPointsCount = dataSet.values.size
  val dataSet = CollectionUtils.createHashMultimap[Array[Double], Array[Double]]

  def addDataPoint(input: Array[Double], output: Array[Double]): ANNTrainingData = {
    require(input.length == inputDimensions && output.length == outputDimensions, "Invalid Dimensions for DataPoint")
    dataSet.addBinding(input, output)
    this
  }

  def addDataPoints(source: ANNTrainingData): ANNTrainingData = {
    addDataPoints(source, this)
  }

  def getDataPoints: List[(Array[Double], Array[Double])] = {
    dataSet.keySet.flatMap { input =>
      val outputs: List[Array[Double]] = dataSet.get(input).get.toList
      outputs.map(output => (input, output))
    }.toList
  }

  def compatibleWith(trainingData: ANNTrainingData): Boolean = {
    trainingData.inputDimensions == inputDimensions && trainingData.outputDimensions == outputDimensions
  }


  def normalised: ANNTrainingData = {
    def computeMagnitude(array: Array[Double]): Double = {
      scala.math.sqrt(array.foldLeft(0.0)((acc, dim) => acc + scala.math.pow(dim, 2)))
    }
    val values = dataSet.values.flatten
    val normalisedDataset = new ANNTrainingData(inputDimensions, outputDimensions)

    if (values.nonEmpty) {
      var minOutput = values.head
      var maxOutput = values.head

      for( value <- values.tail) {
        val magnitude = computeMagnitude(value)

        if (magnitude < computeMagnitude(minOutput)) {
          minOutput = value
        }

        if (magnitude > computeMagnitude(maxOutput)) {
          maxOutput = value
        }
      }

      val maxMinDistance = NumericUtils.combine(maxOutput, minOutput, _ - _)

      for ((key, values) <- dataSet; value <- values) {
        // normalizedPoint = (point - min) / (max - min)
        val distanceFromMin = NumericUtils.combine(value, minOutput, _ - _)
        val normalizedValue = NumericUtils.combine(distanceFromMin, maxMinDistance, _ / _)
        normalisedDataset.addDataPoint(key, normalizedValue)
      }
    }
    normalisedDataset
  }

  def normalisedByDivision: ANNTrainingData = {
    val values = dataSet.values.flatten
    val accumulator = values.headOption
    val normalisedDataset = new ANNTrainingData(inputDimensions, outputDimensions)

    val outputSum = accumulator.map{acc =>
      values.tail.foldLeft(acc)((accum, arr) => NumericUtils.combine(accum, arr, _ + _))
    }

    outputSum.foreach(e => CollectionUtils.print(e))
    outputSum.foreach { outputTotal =>
      for ((key, values) <- dataSet; value <- values) {
        normalisedDataset.addDataPoint(key, NumericUtils.combine(value, outputTotal, _ / _))
      }
    }
    normalisedDataset
  }

  def copy: ANNTrainingData = {
    val dataCopy = new ANNTrainingData(inputDimensions, outputDimensions)

    addDataPoints(this, dataCopy)

    dataCopy
  }

  private def addDataPoints(source: ANNTrainingData, destination: ANNTrainingData): ANNTrainingData = {
    source.getDataPoints.foreach(inOut => destination.addDataPoint(inOut._1, inOut._2))
    destination
  }

  def clear(): Unit = {
    dataSet.clear()
  }

  def saveCSV(path: String): Try[Boolean] = {
    Try{
      val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)))
      getDataPoints.foreach{ case (input, output) =>
          writer.write((input ++ output).mkString(",") + "\n")
      }
      writer.close()
      true
    }
  }
}
