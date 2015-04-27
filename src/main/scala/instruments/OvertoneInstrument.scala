package instruments

import instruments.InstrumentType._
import overtone.wrapper.OvertoneWrapper
import representation.{MusicalElement, Note, Phrase}
import utils.OvertoneUtils

class OvertoneInstrument(val overtoneWrapper: OvertoneWrapper = new OvertoneWrapper(),
                         override val instrumentType: InstrumentType = PIANO) extends Instrument {
  private val overtoneInstrumentType = OvertoneInstrumentType.fromInstrumentType(instrumentType)
  OvertoneUtils.useInstrument(overtoneInstrumentType, overtoneWrapper)

  override def play(musicalElement: MusicalElement): Unit = musicalElement match {
    case note: Note =>
      play(note)
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


