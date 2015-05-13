package training

import cbr.{CaseDescription, CaseIndex}
import midi.JMusicMIDIParser
import representation.Phrase

object TrainingUtils {
  def addCasesToIndex(index: CaseIndex[CaseDescription, Phrase], filename: String): Unit =
    getCasesFromMIDI(filename).foreach(tup => (index.addCase _).tupled(tup))

  def getCasesFromMIDI(filename: String): List[(CaseDescription, Phrase)] = {
    val extractor = new MusicCaseExtractor(JMusicMIDIParser(filename), new MusicalDescriptionCreator)
    extractor.getCases
  }
}
