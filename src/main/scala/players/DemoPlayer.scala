package players

import instruments.{DemoInstrument, Instrument}

class DemoPlayer extends Player {
  override var instrument: Instrument = new DemoInstrument()
}
