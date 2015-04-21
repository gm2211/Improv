package instruments

import instruments.OvertoneInstrumentType._
import overtone.wrapper.OvertoneWrapper
import representation.{Phrase, MusicalElement, Note}
import utils.OvertoneUtils

class OvertoneInstrument(val overtoneWrapper: OvertoneWrapper = new OvertoneWrapper(),
                         val instrumentType: Option[OvertoneInstrumentType] = Some(PIANO)) extends Instrument {
  OvertoneUtils.useInstrument(instrumentType.get, overtoneWrapper)

  override def play(musicalElement: MusicalElement): Unit = musicalElement match {
    case note: Note =>
      play(note)
    case phrase: Phrase =>
      phrase.foreach(play)
  }

  def play(note: Note): Unit = {
    OvertoneUtils.play(
      note = note,
      instrument = instrumentType.get,
      wrapper = overtoneWrapper)
  }
}


