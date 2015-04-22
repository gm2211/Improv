package instruments

import instruments.InstrumentType.InstrumentType

object OvertoneInstrumentType extends Enumeration {
  def fromInstrumentType(instrumentType: InstrumentType): OvertoneInstrumentType = instrumentType match {
    case InstrumentType.PIANO =>
      PIANO
    case InstrumentType.KICK =>
      KICK
  }

  type OvertoneInstrumentType = Value
  val PIANO = Value("piano")
  val SAMPLED_PIANO = Value("sampled-piano")
  val KICK = Value("kick")
  val PING = Value("ping")
}
