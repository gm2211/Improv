package midi

import instruments.InstrumentType.{InstrumentCategory, InstrumentType}
import representation.Phrase

/**
 * Created by gm2211 on 5/5/15.
 */
trait MIDIParser {

  def getPhrases(partNum: Int): Iterator[Option[Phrase]]

  def getPhrase(phraseNum: Int, partNum: Int): Phrase

  def getPartIndexByInstrument: Map[InstrumentType, Array[Int]]

  def getInstrumentsCounts: Map[InstrumentCategory, Int]
}
