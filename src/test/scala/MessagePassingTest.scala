import actors.musicians.AIOvertoneMusician
import akka.actor.ActorSystem
import akka.pattern.Patterns
import akka.testkit.{TestKit, TestActorRef}
import akka.util.Timeout
import instruments.Instrument
import messages.SyncMessage
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.{FiniteDuration, Duration}

class MessagePassingTest extends FlatSpec with MockFactory with Matchers {
  "An actor" should "play something when a sync message is received" in {
    val instrument = mock[Instrument]
    val orchestra = ActorSystem("orchestra")
    val musician = TestActorRef.create(orchestra, AIOvertoneMusician.props(instrument), "MyMusician")

    (instrument.play _).expects(*).returning().once()
    // Directly injecting message in order to avoid concurrency issues
    musician.receive(new SyncMessage())
  }
}
