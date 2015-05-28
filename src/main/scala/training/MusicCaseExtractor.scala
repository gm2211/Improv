package training

import instruments.InstrumentType.InstrumentType
import midi.MIDIParserFactory
import representation.Phrase
import utils.ImplicitConversions.toEnhancedTraversable

import scala.collection.mutable.ListBuffer

/**
 * Extracts cases from a midi file
 */
class MusicCaseExtractor(private val parserFactory: MIDIParserFactory) extends CaseExtractor[(InstrumentType, Phrase)] {



  override def getCases(filename: String): List[((InstrumentType, Phrase), (InstrumentType, Phrase))] = {
    val parser = parserFactory.apply(filename)
    val instrParts: List[(InstrumentType, List[Phrase])] = parser.getPartIndexByInstrument.toList.flatMap {
      case (instrument, partIndices) =>
        partIndices.map(idx => (instrument, parser.getMultiVoicePhrases(idx))).toList
    }
    getAllCases(instrParts)
  }

  def getAllCases(instrParts: List[(InstrumentType, List[Phrase])]) = {
    val indices = instrParts.indices
    val cases = ListBuffer[((InstrumentType, Phrase), (InstrumentType, Phrase))]()

    for (
          (instr, part) <- instrParts;
          (phrase: Phrase, phraseIdx) <- part.zipWithIndex;
          otherPartIdx <- indices
        ) {
      val (otherInstr, otherPart) = instrParts(otherPartIdx)
      if (otherPart.inBounds(phraseIdx + 1)) {
        cases += ( ((instr, phrase), (otherInstr, otherPart(phraseIdx + 1))) )
      }
    }
    cases.toList
  }
}
