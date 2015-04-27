package midi

object MIDIInstrumentCategory {
  type MIDIInstrumentCategory = InstrumentCategory

  sealed abstract class InstrumentCategory(val instrumentNumber: Int)
  case class PIANO(override val instrumentNumber: Int = 0) extends InstrumentCategory(instrumentNumber)
  case class CHROMATIC_PERCUSSION (override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class ORGAN(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class GUITAR(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class BASS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class STRINGS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class ENSEMBLE(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class BRASS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class REED(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class PIPE(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class SYNTH_LEAD(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class SYNTH_PAD(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class SYNTH_EFFECTS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class ETHNIC(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class PERCUSSIVE(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class SOUND_EFFECTS(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)
  case class UNKNOWN(override val instrumentNumber: Int) extends InstrumentCategory(instrumentNumber)

  def classify(instrNumber: Int): InstrumentCategory = instrNumber + 1 match {
    case it if 1 to 8 contains it => PIANO(instrNumber)
    case it if 9 to 16 contains it => CHROMATIC_PERCUSSION(instrNumber)
    case it if 17 to 24 contains it => ORGAN(instrNumber)
    case it if 25 to 32 contains it => GUITAR(instrNumber)
    case it if 33 to 40 contains it => BASS(instrNumber)
    case it if 41 to 48 contains it => STRINGS(instrNumber)
    case it if 49 to 56 contains it => ENSEMBLE(instrNumber)
    case it if 57 to 64 contains it => BRASS(instrNumber)
    case it if 65 to 72 contains it => REED(instrNumber)
    case it if 73 to 80 contains it => PIPE(instrNumber)
    case it if 81 to 88 contains it => SYNTH_LEAD(instrNumber)
    case it if 89 to 96 contains it => SYNTH_PAD(instrNumber)
    case it if 97 to 104 contains it => SYNTH_EFFECTS(instrNumber)
    case it if 105 to 112 contains it => ETHNIC(instrNumber)
    case it if 113 to 120 contains it => PERCUSSIVE(instrNumber)
    case it if 121 to 128 contains it => SOUND_EFFECTS(instrNumber)
    case _ => UNKNOWN(instrNumber)
  }
}

