package instruments

object OvertoneInstrumentType extends Enumeration {
  type OvertoneInstrumentType = Value
  val PIANO = Value("piano")
  val SAMPLED_PIANO = Value("sampled-piano")
  val KICK = Value("kick")
  val PING = Value("ping")
}
