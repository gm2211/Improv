package training

import cbr._
import instruments.InstrumentType.InstrumentType
import midi.MIDIParser
import representation.Phrase
import utils.builders.{Zero, IsOnce, Once, Count}


case class MusicCaseExtractorBuilder[
  ParserCount <: Count,
  DescriptionCreatorCount <: Count] (
    midiParser: Option[MIDIParser] = None,
    descriptionCreator: Option[DescriptionCreator[Phrase]] = None) {

  def withMIDIParser(midiParser: MIDIParser) =
    copy[Once, DescriptionCreatorCount](midiParser = Some(midiParser))

  def withDescriptionCreator(descriptionCreator: DescriptionCreator[Phrase]) =
    copy[ParserCount, Once](descriptionCreator = Some(descriptionCreator))

  def build[A <: ParserCount : IsOnce,
            B <: DescriptionCreatorCount : IsOnce] =
    new MusicCaseExtractor(this.asInstanceOf[MusicCaseExtractorBuilder[Once, Once]])

}

object MusicCaseExtractor {
  def builder = new MusicCaseExtractorBuilder[Zero, Zero]()
}

class MusicCaseExtractor private[training] (builder: MusicCaseExtractorBuilder[Once, Once]) {
  val parser: MIDIParser = builder.midiParser.get
  val descriptionCreator: DescriptionCreator[Phrase] = builder.descriptionCreator.get

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
