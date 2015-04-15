import instruments.OvertoneInstrumentType._
import players.{AIOvertoneMusician, Orchestra}

object Main extends App {
  val orchestra = new Orchestra()
  val instrSet = Set(PIANO, PING, KICK)

  instrSet
    .map(AIOvertoneMusician.createAIMusicianWithInstrType(orchestra.system, _))
    .foreach(orchestra.registerMusician)

  orchestra.start()

  Thread.sleep(2000)
  println("pausing")
  orchestra.pause()
  Thread.sleep(2000)
  println("starting again")
  orchestra.start()
  //orchestra.shutdown(20000L)
}
