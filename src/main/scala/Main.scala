import actors.Orchestra
import actors.musicians.AIMusician
import instruments.OvertoneInstrument
import instruments.OvertoneInstrumentType._
import utils.ImplicitConversions.wrapInOption

object Main extends App {
  //MIDIParser("/Users/gm2211/Documents/imperialCollege/fourthYear/finalProject/codebase/src/main/resources/musicScores/test.mid").getInstrumentsCounts
  DemoOrchestra.run()
}

object DemoOrchestra {
  def run() = {
    val orchestra = new Orchestra()
    val instrSet = Set(PIANO, PING, KICK)

    val musicianBuilder = (instrType: OvertoneInstrumentType) => {
      val instrument = new OvertoneInstrument(instrumentType = instrType)
      AIMusician.builder.withInstrument(instrument)
    }

    instrSet
      .map(musicianBuilder(_).withActorSystem(orchestra.system))
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()

    Thread.sleep(2000)
    println("pausing")
    orchestra.pause()
    Thread.sleep(2000)
    println("starting again")
    orchestra.start()
    orchestra.shutdown(10000L)
  }
}
