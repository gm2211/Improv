package tests

import actors.Orchestra
import actors.musicians.AIMusician
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import tests.TestTags.SlowTest
import _root_.instruments.OvertoneInstrument
import _root_.instruments.OvertoneInstrumentType._
import _root_.utils.ImplicitConversions.wrapInOption

class TunePlayTest extends FlatSpec with MockFactory with Matchers {
  "The orchestra" should "play a pre-composed tune distributedly" taggedAs SlowTest in {
    val orchestra = Orchestra.builder.build
    val instrSet = Set(PIANO, PING, KICK)


    val musicianBuilder = (instrType: OvertoneInstrumentType) => {
//      val instrument = mock[Instrument]
//      (instrument.play _).expects(*) //TODO: Actually verify that the right thing is played
      val instrument = new OvertoneInstrument
      AIMusician.builder.withInstrument(instrument)
    }

    instrSet
      .map(musicianBuilder(_).withActorSystem(orchestra.system))
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()
    Thread.sleep(10000L)
    orchestra.shutdown()
  }
}
