package midi

import jm.music.data.Score
import jm.util.Read

object MIDIParser {
  def apply(filename: String) = {
    val score: Score = new Score()
    Read.midi(score, filename)
    new MIDIParser(score)
  }
}

class MIDIParser(val score: Score) {

  def getInstrumentsCounts: Map[MIDIInstrumentCategory.InstrumentCategory, Int] = {
    val parts = score.getPartArray
    val instruments = parts.map(i => MIDIInstrumentCategory.classify(i.getInstrument))
    instruments.groupBy(identity).mapValues(_.length)
  }
}
