package instruments

import instruments.{InstrumentType => instrType}
import utils.CollectionUtils

object OvertoneInstrumentType extends Enumeration {
  def fromInstrumentType(instrumentType: instrType.InstrumentType): OvertoneInstrumentType = instrumentType match {
    case instrType.PIANO(_) =>
      SAMPLED_PIANO
    case instrType.PERCUSSIVE(_) | instrType.CHROMATIC_PERCUSSION(_) =>
      CollectionUtils.chooseRandom(List(KICK, KICK2, KICK3, KICK4, DRY_KICK, DUB_KICK, SNARE, CLAP)).get
    case _ =>
      PING
  }

  type OvertoneInstrumentType = Value
  val PIANO = Value("piano")
  val SAMPLED_PIANO = Value("sampled-piano")
  val KICK = Value("kick")
  val KICK2 = Value("kick2")
  val KICK3 = Value("kick3")
  val KICK4 = Value("kick4")
  val DUB_KICK = Value("dub-kick")
  val DRY_KICK = Value("dry-kick")
  val SMALL_HAT = Value("small-hat")
  val SNARE = Value("snare")
  val CLAP = Value("clap")
  val PING = Value("ping")
}
