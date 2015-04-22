import actors.Orchestra
import actors.musicians.AIMusician
import akka.actor.ActorSystem
import instruments.InstrumentType._
import instruments.JFugueInstrument
import utils.ImplicitConversions.wrapInOption

object Main extends App {
  //MIDIParser("/Users/gm2211/Documents/imperialCollege/fourthYear/finalProject/codebase/src/main/resources/musicScores/test.mid").getInstrumentsCounts
  DemoOrchestra.run()
}

object DemoOrchestra {
  def run() = {
    val orchestra = new Orchestra()
    val instrSet = Set(PIANO, PIANO, KICK)

    val musicianBuilder = (instrType: InstrumentType) => {
      val instrument = new JFugueInstrument(instrType)
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
