import instruments.OvertoneInstrumentType._
import players.{AIOvertoneMusician, Orchestra}

object Main extends App {
  val orchestra = new Orchestra()
  val instrSet = Set(PIANO, TICKER, KICK)

  instrSet
    .map(AIOvertoneMusician.createAIMusicianWithInstrType(orchestra.system, _))
    .foreach(orchestra.registerMusician)

  orchestra.start()
  orchestra.shutdown(20000L)
}
