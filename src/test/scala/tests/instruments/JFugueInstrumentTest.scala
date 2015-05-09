package tests.instruments

import designPatterns.observer.{EventNotification, Observer}
import instruments.InstrumentType.PIANO
import instruments.{FinishedPlaying, JFugueInstrument}
import org.scalatest.FlatSpec
import representation.{Note, Phrase, Rest}
import tests.TestTags.SlowTest

class JFugueInstrumentTest extends FlatSpec {
  var instrument: JFugueInstrument = _
  val lock = new AnyRef

  def setup() = {
    instrument = new JFugueInstrument(PIANO())
  }

  "An instrument" should "play a phrase note by note" taggedAs SlowTest in {
    myTest()
  }

  def myTest() = {
    setup()
    val phrase = Phrase()
      .addMusicalElement(Note.fromString("A5").withDuration(1))
      .addMusicalElement(new Rest(0.1))
      .addMusicalElement(Note.fromString("B5").withDuration(1))
      .addMusicalElement(new Rest(0.1))
      .addMusicalElement(Note.fromString("C5").withDuration(1))

    val listener = new Observer {
      override def notify(eventNotification: EventNotification): Unit = eventNotification match {
        case FinishedPlaying =>
          println("FinishedPlaying")
          lock.synchronized(lock.notifyAll())
      }
    }
    instrument.addObserver(listener)
    instrument.play(phrase)
    while (!instrument.finishedPlaying) {
      lock.synchronized(lock.wait())
    }
  }
}
