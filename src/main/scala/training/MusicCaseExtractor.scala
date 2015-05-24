package training

import instruments.InstrumentType.InstrumentType
import midi.MIDIParserFactory
import representation.Phrase

import scala.collection.mutable.ListBuffer

/**
 * Extracts cases from a midi file
 */
class MusicCaseExtractor (private val parserFactory: MIDIParserFactory) extends CaseExtractor[Phrase] {
  override def getCases(filename: String): List[(Phrase, Phrase)] = {
    val parser = parserFactory.apply(filename)
    parser.getPartIndexByInstrument.toList.flatMap { case (instrumentType, partIndices) =>
      val parts = partIndices.map(parser.getMultiVoicePhrases).toList
      getCasesFromParts(parts, instrumentType)
    }
  }

  private def getCasesFromParts(
      parts: List[List[Phrase]],
      instrumentType: InstrumentType): List[(Phrase, Phrase)] = {
    parts.flatMap(part => getCasesFromPart(part, instrumentType))
  }

  private def getCasesFromPart(
      partPhrases: List[Phrase],
      instrumentType: InstrumentType): List[(Phrase, Phrase)] = {
    val cases = ListBuffer[(Phrase, Phrase)]()
    for (idx <- 1 until partPhrases.size) {
      cases += (( partPhrases(idx - 1), partPhrases(idx)))
    }
    cases.toList
  }
}
