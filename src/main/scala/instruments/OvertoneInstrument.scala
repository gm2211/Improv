package instruments

import instruments.OvertoneInstrumentType._
import overtone.wrapper.OvertoneWrapper
import representation.Note
import utils.OvertoneUtils

class OvertoneInstrument(val overtoneWrapper: OvertoneWrapper = new OvertoneWrapper(),
                         val instrumentType: Option[OvertoneInstrumentType] = Some(PIANO)) extends Instrument {
  OvertoneUtils.useInstrument(instrumentType.get, overtoneWrapper)

  def play(note: Note): Unit =
    OvertoneUtils.play(
      note = note,
      instrument = instrumentType.get,
      wrapper = overtoneWrapper)
}


