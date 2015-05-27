package midi.segmentation

import representation.{MusicalElement, Phrase}
import utils.ImplicitConversions.toEnhancedTraversable
import utils.NumericUtils

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.SECONDS

object PhraseSegmenter {
  val DEFAULT_SUB_PHRASE_LENGTH_NS: BigInt = SECONDS.toNanos(10)

  def getDefault(splitEveryNS: BigInt = DEFAULT_SUB_PHRASE_LENGTH_NS): PhraseSegmenter =
    new SimpleSplitTimesFinder(splitEveryNS) with PhraseSegmenter
}

trait PhraseSegmenter {
  this: SplitTimesFinder =>

  def split(phrase: Phrase): List[Phrase] = {
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
