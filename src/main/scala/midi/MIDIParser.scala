package midi

import instruments.InstrumentType.{InstrumentCategory, InstrumentType}
import representation.Phrase

trait MIDIParser {

  def getPhrases(partNum: Int): Iterator[Phrase]

  def getPhrase(phraseNum: Int, partNum: Int): Phrase

  def getPartIndexByInstrument: Map[InstrumentType, Array[Int]]

  def getInstrumentsCounts: Map[InstrumentCategory, Int]
}
