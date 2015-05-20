package midi.segmentation

import representation.{MusicalElement, Phrase}
import utils.ImplicitConversions.{toEnhancedTraversable, toLong}
import utils.NumericUtils

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Try

object SimpleSegmenter {
  val DEFAULT_SUB_PHRASE_LENGTH_BPM: BigDecimal = 10

  def getDefault = new SimpleSegmenter() {
    override def getSplitTimes(phrase: Phrase): Traversable[BigInt] = {
      val upperBound = phrase.getStartTimeNS + phrase.getDurationNS
      val step = MusicalElement.fromBPM(DEFAULT_SUB_PHRASE_LENGTH_BPM, phrase.tempoBPM)
      step to upperBound by step
    }
  }
}

abstract class SimpleSegmenter extends PhraseSegmenter {
  protected def getSplitTimes(phrase: Phrase): Traversable[BigInt]

  override def split(phrase: Phrase): List[Phrase] = phrase match {
    case p@Phrase(_, true, _) =>
      splitPolyphonic(p)
    case p@Phrase(_, false, _) =>
      splitPhrase(p)
  }

  //TODO: Rewrite this more simply
  private def splitPolyphonic(polyphonicPhrase: Phrase) = {
    require(polyphonicPhrase.polyphony)
    val resultPhrases = ListBuffer[Phrase]()
    var currentPhrases = polyphonicPhrase.map(_ => ListBuffer[MusicalElement]()).toList
    val elements: List[Iterator[MusicalElement]] = polyphonicPhrase.musicalElements.map(_.asInstanceOf[Phrase].toIterator)
    var activeElements = {
      mutable.MutableList(elements
        .toStream
        .zipWithIndex
        .collect { case (it, idx) if it.hasNext => (idx, it.next()) }: _*)
    }
    var inactiveElements = mutable.MutableList[(Int, MusicalElement)]()
    val tempoBPM = polyphonicPhrase.tempoBPM
    var splitTimes = getSplitTimes(polyphonicPhrase)
    var curTimeNS: BigInt = getMinStartTime(activeElements)

    while (activeElements.nonEmpty && splitTimes.nonEmpty) {
      val (act, inact) = activeElements.partition{ case (idx, elem) => MusicalElement.isActive(curTimeNS, elem) }
      activeElements = act
      inactiveElements = inact
      inactiveElements.foreach{ case (idx, elem) =>
        currentPhrases(idx) += elem
        Try(elements(idx).next()).foreach(newElem => activeElements.+=((idx, newElem)))
      }
      curTimeNS += getMinDuration(activeElements)

      if (curTimeNS >= splitTimes.head) {
        splitTimes = splitTimes.tail
        val (newAct, newPhrase, newCurrPhrases) = createPhrases(activeElements, currentPhrases, curTimeNS, tempoBPM)
        activeElements = newAct
        newPhrase.foreach(resultPhrases.+=)
        currentPhrases = newCurrPhrases
      }
    }
    val (_, newPhrase, _) = createPhrases(mutable.MutableList(), currentPhrases, 0, tempoBPM, last = true)
    (resultPhrases ++ newPhrase).toList
  }

  private def splitPhrase(phrase: Phrase): List[Phrase] = {
    var curPhrase = Option(phrase)
    val phrases = ListBuffer[Phrase]()

    for (curTime <- getSplitTimes(phrase) if curPhrase.isDefined) {
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

    if (! last) {
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
    activeElements.foreach{ case (idx, element) =>
      val (fstHalf, sndHalf) = MusicalElement.split(element, curTime)
      fstHalf.foreach(elem => elementsToBeAdded.+=((idx, elem)))
      sndHalf.foreach(elem => newActiveElements.+=((idx, elem)))
    }
    (elementsToBeAdded.toList, newActiveElements)
  }
}
