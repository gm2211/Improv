package training

import cbr.CaseIndex
import midi.JMusicMIDIParser
import representation.Phrase

object TrainingUtils {
  def addCasesToIndex(index: CaseIndex[Phrase], filename: String): Unit =
    getCasesFromMIDI(filename).foreach{ case (desc, sol) => index.addSolutionToProblem(desc, sol) }

  def getCasesFromMIDI(filename: String): List[(Phrase, Phrase)] = {
    val extractor = new MusicCaseExtractor(JMusicMIDIParser)

    extractor.getCases(filename)
  }
}
