package midi

import instruments.InstrumentType.{InstrumentCategory, InstrumentType}
import representation.Phrase

trait MIDIParserFactory {
  // TODO: Define exactly what length means
  val DEFAULT_PHRASE_LENGTH = 10

  def apply(filename: String, phraseLength: Int = DEFAULT_PHRASE_LENGTH): MIDIParser
}

trait MIDIParser {

  def getPhrases(partNum: Int): Iterator[Phrase]

  def getPhrase(partNum: Int, phraseNum: Int): Option[Phrase]

  def getPartIndexByInstrument: Map[InstrumentType, Array[Int]]

  def getInstrumentsCounts: Map[InstrumentCategory, Int]
}
