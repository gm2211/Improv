package midi

import instruments.InstrumentType.InstrumentType
import representation.Phrase

import scala.collection.mutable

trait MIDIParserFactory {
  def apply(filename: String): MIDIParser
}

trait MIDIParser {

  def getPhrases(partNum: Int): Traversable[Phrase]

  def getMultiVoicePhrases(partNum: Int): Traversable[Phrase]

  def getPartIndexByInstrument: mutable.MultiMap[InstrumentType, Int]
}
