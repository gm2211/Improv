package midi.segmentation

import representation.{MusicalElement, Phrase}
import utils.ImplicitConversions.toEnhancedTraversable
import utils.NumericUtils

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.SECONDS

object SimpleSegmenter {
  val DEFAULT_SUB_PHRASE_LENGTH_NS: BigInt = SECONDS.toNanos(2)

  def getDefault(splitEveryNS: BigInt = DEFAULT_SUB_PHRASE_LENGTH_NS) = new SimpleSegmenter() {
    override def getSplitTimes(phrase: Phrase): Traversable[BigInt] = {
      splitEveryNS to phrase.getEndTimeNS by splitEveryNS
    }
  }
}

abstract class SimpleSegmenter extends PhraseSegmenter {
  protected def getSplitTimes(phrase: Phrase): Traversable[BigInt]

  override def split(phrase: Phrase): List[Phrase] = {
    var curPhrase = Option(phrase)
    val phrases = ListBuffer[Phrase]()
    val splitTimes = getSplitTimes(phrase)

    if (splitTimes.isEmpty) {
      return List(phrase)
    }

    for (curTime <- splitTimes if curPhrase.isDefined) {
      val (newPhrase, rest) = Phrase.split(phrase, curTime)
      newPhrase.foreach(phrases.+=)
      curPhrase = rest
    }
    phrases.toList
  }

  private def createPhrases(
    activeElements: mutable.MutableList[(Int, MusicalElement)],
    currentPhrases: List[ListBuffer[MusicalElement]],
    curTimeNS: BigInt,
    tempoBPM: Double,
    last: Boolean = false) = {
    val (splitElements, newActiveElements) = splitElementsAcrossBoundary(activeElements, curTimeNS)

    if (!last) {
      splitElements.foreach { case (idx, elem) => currentPhrases(idx) += elem }
    }

    val resultPhrase = Phrase(currentPhrases, tempoBPM)
    val newCurrentPhrases = currentPhrases.map(_ => ListBuffer[MusicalElement]())

    (newActiveElements, resultPhrase, newCurrentPhrases)
  }

  def getMinStartTime(activeElements: mutable.MutableList[(Int, MusicalElement)]): BigInt = {
    def min(prev: BigInt, t2: (Int, MusicalElement)): BigInt =
      NumericUtils.min(prev, t2._2.getStartTimeNS)

    activeElements.foldLeftWithFstAsDefault(BigInt(0), _._2.getStartTimeNS, min)
  }

  private def getMinDuration(activeElements: mutable.MutableList[(Int, MusicalElement)]): BigInt = {
    def min(prev: BigInt, t2: (Int, MusicalElement)): BigInt =
      NumericUtils.min(prev, t2._2.getDurationNS)

    activeElements.foldLeftWithFstAsDefault(BigInt(0), _._2.getDurationNS, min)
  }


  private def splitElementsAcrossBoundary(activeElements: Traversable[(Int, MusicalElement)], curTime: BigInt) = {
    val elementsToBeAdded = ListBuffer[(Int, MusicalElement)]()
    val newActiveElements = mutable.MutableList[(Int, MusicalElement)]()
    activeElements.foreach { case (idx, element) =>
      val (fstHalf, sndHalf) = MusicalElement.split(element, curTime)
      fstHalf.foreach(elem => elementsToBeAdded.+=((idx, elem)))
      sndHalf.foreach(elem => newActiveElements.+=((idx, elem)))
    }
    (elementsToBeAdded.toList, newActiveElements)
  }
}
