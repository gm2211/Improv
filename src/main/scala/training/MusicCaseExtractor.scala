package training

import cbr.description.{CaseDescription, DescriptionCreator}
import instruments.InstrumentType.InstrumentType
import midi.{MIDIParserFactory, MIDIParser}
import representation.Phrase
import utils.builders.{Count, IsOnce, Once, Zero}


case class MusicCaseExtractorBuilder[
  ParserCount <: Count,
  DescriptionCreatorCount <: Count] (
    midiParserFactory: Option[MIDIParserFactory] = None,
    descriptionCreator: Option[DescriptionCreator[Phrase]] = None) {

  def withMIDIParser(mIDIParserFactory: MIDIParserFactory) =
    copy[Once, DescriptionCreatorCount](midiParserFactory = Some(midiParserFactory))

  def withDescriptionCreator(descriptionCreator: DescriptionCreator[Phrase]) =
    copy[ParserCount, Once](descriptionCreator = Some(descriptionCreator))

  def build[A <: ParserCount : IsOnce,
            B <: DescriptionCreatorCount : IsOnce] =
    new MusicCaseExtractor(this.asInstanceOf[MusicCaseExtractorBuilder[Once, Once]])
}

object MusicCaseExtractor {
  def builder = new MusicCaseExtractorBuilder[Zero, Zero]()
}

/**
 * Extracts cases from a midi file
 */
class MusicCaseExtractor (builder: MusicCaseExtractorBuilder[Once, Once]) extends CaseExtractor[Phrase] {
  private val parserFactory: MIDIParserFactory = builder.midiParserFactory.get
  private val descriptionCreator: DescriptionCreator[Phrase] = builder.descriptionCreator.get

  override def getCases(filename: String): List[(CaseDescription, Phrase)] = {
    val parser = parserFactory.apply(filename)
    parser.getPartIndexByInstrument.flatMap { case (instrumentType, partIndices) =>
      val parts = partIndices.map(parser.getMultiVoicePhrases)
      partIndices.map(getCasesFromParts(parts, instrumentType))
    }.toList
  }

  private def getCasesFromParts(
    parts: Traversable[Traversable[Phrase]],
    instrumentType: InstrumentType): List[(CaseDescription, Phrase)] =
    parts.flatMap(part => getCasesFromPart(part, instrumentType)).toList

  private def getCasesFromPart(partPhrases: Traversable[Phrase], instrumentType: InstrumentType): List[(CaseDescription, Phrase)] = {
    var curPhrase = partPhrases.headOption

    for (nextPhrase <- partPhrases.drop(1).toList) yield {
      val description = descriptionCreator.createCaseDescription(curPhrase.get)
      val musicalCase = (description, nextPhrase)
      curPhrase = Some(nextPhrase)
      musicalCase
    }
  }
}
