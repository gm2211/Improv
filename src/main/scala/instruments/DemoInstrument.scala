package instruments

class DemoInstrument extends OvertoneInstrument {
  val DEMO = "(demo (sin-osc))"
  override def play(): Unit = overtoneWrapper.sendCommand(DEMO)
}
