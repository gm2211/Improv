package instruments

import instruments.OvertoneInstrumentType._
import overtone.wrapper.OvertoneWrapper
import representation.{MusicalElement, Note}
import utils.OvertoneUtils

class OvertoneInstrument(val overtoneWrapper: OvertoneWrapper = new OvertoneWrapper(),
                         val instrumentType: Option[OvertoneInstrumentType] = Some(PIANO)) extends Instrument {
  OvertoneUtils.useInstrument(instrumentType.get, overtoneWrapper)

  override def play(musicalElement: MusicalElement): Unit = musicalElement match {
    case note: Note =>
      OvertoneUtils.play (
      note = note,
      instrument = instrumentType.get,
      wrapper = overtoneWrapper)
    case _ =>
      ()
  }
}


