package training

import cbr.CaseIndex
import instruments.InstrumentType.InstrumentType
import midi.JMusicMIDIParser
import representation.Phrase

object TrainingUtils {
  def addCasesToIndex(index: CaseIndex[(InstrumentType, Phrase)], filename: String): Unit =
    getCasesFromMIDI(filename).foreach{ case (desc, sol) => index.addSolutionToProblem(desc, sol) }

  def getCasesFromMIDI(filename: String): List[((InstrumentType, Phrase), (InstrumentType, Phrase))] = {
    val extractor = new MusicCaseExtractor(JMusicMIDIParser)

    extractor.getCases(filename)
  }
}
