package midi

import instruments.InstrumentType.{InstrumentCategory, InstrumentType}
import representation.Phrase

import scala.collection.mutable

trait MIDIParserFactory {
  // TODO: Define exactly what length means
  val DEFAULT_PHRASE_LENGTH = 10

  def apply(filename: String, phraseLength: Int = DEFAULT_PHRASE_LENGTH): MIDIParser
}

trait MIDIParser {

  def getPhrases(partNum: Int): Traversable[Phrase]

  def getMultiVoicePhrases(partNum: Int): Traversable[Phrase]

  def getPartIndexByInstrument: mutable.MultiMap[InstrumentType, Int]

  def getInstrumentsCounts: Map[InstrumentCategory, Int]
}
