package instruments

import instruments.InstrumentType.{InstrumentType, PIANO}
import overtone.wrapper.OvertoneWrapper
import representation.{MusicalElement, Note, Phrase, Rest}
import utils.OvertoneUtils

class OvertoneInstrument(val overtoneWrapper: OvertoneWrapper = new OvertoneWrapper(),
  override val instrumentType: InstrumentType = PIANO()) extends Instrument {
  private val overtoneInstrumentType = OvertoneInstrumentType.fromInstrumentType(instrumentType)
  OvertoneUtils.useInstrument(overtoneInstrumentType, overtoneWrapper)

  override def play(musicalElement: MusicalElement): Unit = musicalElement match {
    case note: Note =>
      play(note)
    case rest: Rest =>
      Thread.sleep(rest.durationSec.toInt)
    case phrase: Phrase =>
      phrase.foreach(play)
  }

  def play(note: Note): Unit = {
    OvertoneUtils.play(
      note = note,
      instrument = overtoneInstrumentType,
      wrapper = overtoneWrapper)
  }
}


