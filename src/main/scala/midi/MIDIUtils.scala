package midi

import javax.sound.midi.Sequence

import instruments.JFugueUtils
import representation.Phrase

object MIDIUtils {

  def toSequence(phrase: Phrase, instrumentNumber: Int): Sequence = {
    val pattern = JFugueUtils.createPattern(phrase, instrumentNumber)
    JFugueUtils.toSequence(pattern)
  }

}
