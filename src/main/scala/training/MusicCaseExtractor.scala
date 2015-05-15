package training

import cbr._
import instruments.InstrumentType.InstrumentType
import midi.MIDIParser
import representation.Phrase

class MusicCaseExtractor(val parser: MIDIParser, val descriptionCreator: MusicalDescriptionCreator) {
  def getCases: List[(CaseDescription, Phrase)] = {
    parser.getPartIndexByInstrument.flatMap { case (instrumentType, partIndices) =>
      partIndices.map(getCasesFromParts(partIndices.toSet, instrumentType))
    }.toList
  }

  def getCasesFromParts(partIndices: Set[Int], instrumentType: InstrumentType): List[(CaseDescription, Phrase)] =
    partIndices.flatMap(partIndex => getCasesFromPart(partIndex, instrumentType)).toList

  def getCasesFromPart(partIndex: Int, instrumentType: InstrumentType): List[(CaseDescription, Phrase)] = {
    val phrases = parser.getMultiVoicePhrases(partIndex)
    var curPhrase = phrases.headOption

    for (nextPhrase <- phrases.drop(1).toList) yield {
      val description = descriptionCreator.createCaseDescription(curPhrase.get)
      val musicalCase = (description, nextPhrase)
      curPhrase = Some(nextPhrase)
      musicalCase
    }
  }
}
