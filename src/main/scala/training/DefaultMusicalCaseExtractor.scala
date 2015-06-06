package training

import cbr.MusicalCase
import instruments.InstrumentType.InstrumentType
import midi.{JMusicMIDIParser, MIDIParserFactory}
import representation.Phrase
import utils.ImplicitConversions.toEnhancedIterable

import scala.collection.mutable.ListBuffer

object MusicalCaseExtractors {
  def getDefault(parserFactory: MIDIParserFactory = JMusicMIDIParser) =
    new DefaultMusicalCaseExtractor(parserFactory)
}
/**
 * Extracts cases from a midi file
 */
class DefaultMusicalCaseExtractor(private val parserFactory: MIDIParserFactory) extends CaseExtractor[MusicalCase] {
  override def getCases(filename: String): List[(MusicalCase, MusicalCase)] = {
    val parser = parserFactory.apply(filename)
    val instrParts: List[(InstrumentType, List[Phrase])] = parser.getPartIndexByInstrument.toList.flatMap {
      case (instrument, partIndices) =>
        partIndices.map(idx => (instrument, parser.getMultiVoicePhrases(idx))).toList
    }
    getAllCases(instrParts)
  }

  def getAllCases(instrParts: List[(InstrumentType, List[Phrase])]) = {
    val indices = instrParts.indices
    val cases = ListBuffer[(MusicalCase, MusicalCase)]()

    for (
          (instr, part) <- instrParts;
          (phrase: Phrase, phraseIdx) <- part.zipWithIndex;
          otherPartIdx <- indices
        ) {
      val (otherInstr, otherPart) = instrParts(otherPartIdx)
      if (otherPart.inBounds(phraseIdx + 1)) {
        cases += ( (MusicalCase(instr, phrase), MusicalCase(otherInstr, otherPart(phraseIdx + 1))) )
      }
    }
    cases.toList
  }
}


