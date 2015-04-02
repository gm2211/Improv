package instruments

import overtone.wrapper.OvertoneWrapper

trait Instrument {
  val overtoneWrapper = new OvertoneWrapper()
  def play(): Unit
}
