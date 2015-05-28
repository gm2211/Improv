package training

import cbr.{CaseIndex, MusicalCase}
import midi.JMusicMIDIParser

object TrainingUtils {
  def addCasesToIndex(index: CaseIndex[MusicalCase], filename: String): Unit = {
    val cases = getCasesFromMIDI(filename)
    cases.foreach { case (desc, sol) => index.addSolutionToProblem(desc, sol) }
  }

  def getCasesFromMIDI(filename: String): List[(MusicalCase, MusicalCase)] = {
    val extractor = new MusicalCaseExtractor(JMusicMIDIParser)

    extractor.getCases(filename)
  }
}
