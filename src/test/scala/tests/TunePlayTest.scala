package tests

import _root_.instruments.OvertoneInstrument
import _root_.instruments.OvertoneInstrumentType._
import actors.Orchestra
import actors.musicians.AIMusician
import actors.musicians.AIMusician._
import actors.musicians.behaviour.SyncMessageReceivedBehaviour
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import tests.TestTags.SlowTest

class TunePlayTest extends FlatSpec with MockFactory with Matchers {
  "The orchestra" should "play a pre-composed tune distributedly" taggedAs SlowTest in {
    val orchestra = Orchestra.builder.build
    val instrSet = Set(PIANO, PING, KICK)


    val musicianBuilder = (instrType: OvertoneInstrumentType) => {
      val instrument = new OvertoneInstrument
      AIMusician.builder.withInstrument(instrument).addBehaviour(new SyncMessageReceivedBehaviour)
    }

    instrSet
      .map(musicianBuilder(_))
      .foreach(m => orchestra.registerMusician(m))

    orchestra.start()
    Thread.sleep(10000L)
    orchestra.shutdown()
  }
}
