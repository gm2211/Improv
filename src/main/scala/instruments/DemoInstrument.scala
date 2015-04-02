package instruments

import overtone.wrapper.OvertoneWrapper

class DemoInstrument extends Instrument {
  override def play(): Unit = overtoneWrapper.sendCommand(OvertoneWrapper.DEMO);
}
