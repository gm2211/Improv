package players

import instruments.Instrument

trait Player {
  var instrument: Instrument
  def play(): Unit = instrument.play();
}
