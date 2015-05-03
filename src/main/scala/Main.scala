import actors.Orchestra
import actors.composers.MIDIReaderComposer
import actors.directors.SimpleDirector
import actors.musicians.AIMusician
import instruments.InstrumentType.{InstrumentType, PIANO}
import instruments.{Instrument, JFugueInstrument}
import midi.MIDIParser
import org.slf4j.LoggerFactory
import utils.ImplicitConversions.{anyToRunnable, wrapInOption}

object Main extends App {
  //MIDIParser("/Users/gm2211/Documents/imperialCollege/fourthYear/finalProject/codebase/src/main/resources/musicScores/test.mid").getInstrumentsCounts
//  DemoMIDIOrchestra.run(getClass.getClassLoader.getResource("musicScores/test.mid").getPath)
  DemoRandomOrchestra.run()
//  DemoActors.run()
}

object DemoActors {
  import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

  import scala.collection.mutable

  trait Listener {
    def onDone()
  }

  class B {
    val listeners = mutable.MutableList[Listener]()
    def addListener(l: Listener): Unit = listeners += l
    def doWork() = {
      Thread.sleep(10000)
      listeners.foreach(_.onDone())
    }
  }

  class A extends Actor with ActorLogging with Listener {
    private var done = true
    override def receive: Receive = {
      case _ =>
        log.debug("Received")
        if (done) {
          log.debug("Processing message")
          done = false
          val b = new B
          b.addListener(this)
          new Thread(() => b.doWork()).start()
        } else {
          log.debug("Busy processing. Ignoring message")
        }
    }

    override def onDone(): Unit = done = true
  }

  def run(): Unit = {
    val log = LoggerFactory.getLogger(getClass)
    val actorSystem = ActorSystem.create("asd")
    val a = actorSystem.actorOf(Props(new A))
    (1 to 10).foreach{ i =>
      log.debug("Sending message")
      a ! "hello"
      log.debug("Sleeping in while")
      Thread.sleep(2000)
    }
  }
}

object DemoMIDIOrchestra {
  def run(filename: String) = {
    val director = Option(SimpleDirector.builder.withSyncFrequencyMS(1000L))
    val orchestra = Orchestra.builder.withDirector(director).build
    val parser = MIDIParser(filename)

    val musicianBuilder = (instrType: InstrumentType, partNumber: Int) => {
      val instrument = new JFugueInstrument(instrType)
      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(new MIDIReaderComposer(filename, partNumber))
    }

    for ((instrument, parts) <- parser.getPartIndexByInstrument) {
      parts.map (musicianBuilder (instrument, _).withActorSystem (orchestra.system) )
           .foreach (m => orchestra.registerMusician (m.build) )
      }

    orchestra.start()
  }
}

object DemoRandomOrchestra {
  def run() = {
    val orchestra = Orchestra.builder.build
    val instrSet = Set(PIANO())

    val musicianBuilder = (instrType: InstrumentType) => {
      val instrument = new JFugueInstrument(instrumentType = instrType)
      AIMusician.builder
        .withInstrument(instrument)
    }

    instrSet
      .map(t => musicianBuilder(t).withActorSystem(orchestra.system))
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()

  }
}

object DemoThreads {
  def run() = {
    val i1: Instrument = new JFugueInstrument()
    val i2: Instrument = new JFugueInstrument()

    val p1: Runnable = () => i1.play(representation.Note.fromString("C5b"))
    val p2: Runnable = () => i1.play(representation.Note.fromString("D5b"))

    val t1 = new Thread(p1)
    val t2 = new Thread(p2)

    t1.run()
    t2.run()
  }
}
