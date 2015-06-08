package training.segmentation

import representation.Phrase

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.SECONDS

object PhraseSegmenter {
  private val TIME_WARP = 3 // 1s in the system corresponds to 3s in real life
  val DEFAULT_SUB_PHRASE_LENGTH_NS: BigInt = SECONDS.toNanos(5) / TIME_WARP

  def getDefault(splitEveryNS: BigInt = DEFAULT_SUB_PHRASE_LENGTH_NS): PhraseSegmenter =
    new SimpleSplitTimesFinder(splitEveryNS) with PhraseSegmenter
}

trait PhraseSegmenter {
  this: SplitTimesFinder =>

  def segment(phrase: Phrase): List[Phrase] = {
    var curPhrase = Option(phrase)
    val phrases = ListBuffer[Phrase]()
    val splitTimes = getSplitTimes(phrase)

    if (splitTimes.isEmpty) {
      return List(phrase)
    }

    for (curTime <- splitTimes if curPhrase.isDefined) {
      val (newPhrase, rest) = Phrase.split(curPhrase.get, curTime)
      newPhrase.foreach(phrases.+=)
      curPhrase = rest
    }
    phrases.toList
  }
}
