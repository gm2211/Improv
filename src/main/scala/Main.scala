import overtone.wrapper.OvertoneWrapper

object Main extends App {
  var overtone = new OvertoneWrapper()
  overtone.sendCommand("(demo (sin-osc))")
}
