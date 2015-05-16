package training

import cbr.{CaseDescription, CaseIndex}
import instruments.InstrumentType.PIANO
import midi.JMusicMIDIParser
import representation.Phrase

object TrainingUtils {
  def addCasesToIndex(index: CaseIndex[CaseDescription, Phrase], filename: String): Unit =
    getCasesFromMIDI(filename).foreach(tup => (index.addCase _).tupled(tup))

  def getCasesFromMIDI(filename: String): List[(CaseDescription, Phrase)] = {
    val extractor = MusicCaseExtractor.builder
      .withMIDIParser(JMusicMIDIParser(filename))
      .withDescriptionCreator(new PhraseFeatureExtractor with DescriptionCreator[Phrase])
      .build

    extractor.getCasesFromPart(0, PIANO(1))
  }
}
