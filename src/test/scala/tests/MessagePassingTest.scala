package tests

import _root_.instruments.Instrument
import actors.musicians.AIMusicianBuilder
import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActorRef
import messages.SyncMessage
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}
import _root_.utils.ImplicitConversions._

class MessagePassingTest extends FlatSpec with MockFactory with Matchers {
  "An actor" should "play something when a sync message is received" in {
    val instrument = mock[Instrument]
    val orchestra = ActorSystem("orchestra")
    val musician = TestActorRef.create(orchestra, Props(AIMusicianBuilder(instrument, orchestra, None).build), "MyMusician")

    (instrument.play _).expects(*).returning().once()
    // Directly injecting message in order to avoid concurrency issues
    musician.receive(new SyncMessage(1))
  }
}