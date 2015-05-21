package training

import cbr.CaseIndex
import cbr.description.CaseDescription
import cbr.description.features.PhraseFeatureExtractor
import instruments.InstrumentType.PIANO
import midi.JMusicMIDIParser
import representation.Phrase

object TrainingUtils {
  def addCasesToIndex(index: CaseIndex[CaseDescription, Phrase], filename: String): Unit =
    getCasesFromMIDI(filename).foreach(tup => (index.addCase _).tupled(tup))

  def getCasesFromMIDI(filename: String): List[(CaseDescription, Phrase)] = {
    val extractor = MusicCaseExtractor.builder
      .withMIDIParser(JMusicMIDIParser(filename))
      .withDescriptionCreator(PhraseFeatureExtractor.getDefaultExtractor)
      .build

    extractor.getCasesFromPart(0, PIANO(1))
  }
}
