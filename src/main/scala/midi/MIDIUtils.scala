package midi

import javax.sound.midi.Sequence

import instruments.JFugueUtils
import representation.MusicalElement

object MIDIUtils {

  def toSequence(musicalElement: MusicalElement, instrumentNumber: Int): Sequence = {
    val pattern = JFugueUtils.createPattern(musicalElement, instrumentNumber)
    JFugueUtils.toSequence(pattern)
  }

}
