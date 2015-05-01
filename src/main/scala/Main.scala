import actors.Orchestra
import actors.composers.MIDIReaderComposer
import actors.musicians.AIMusician
import instruments.InstrumentType.{PIANO, PERCUSSIVE, InstrumentType}
import instruments.{Instrument, JFugueInstrument, OvertoneInstrument}
import midi.MIDIParser
import utils.ImplicitConversions.{anyToRunnable, wrapInOption}

object Main extends App {
  //MIDIParser("/Users/gm2211/Documents/imperialCollege/fourthYear/finalProject/codebase/src/main/resources/musicScores/test.mid").getInstrumentsCounts
  DemoMIDIOrchestra.run(getClass.getClassLoader.getResource("musicScores/test.mid").getPath)
  //DemoRandomOrchestra.run()
}

object DemoMIDIOrchestra {
  def run(filename: String) = {
    val orchestra = new Orchestra()
    val parser = MIDIParser(filename)

    val musicianBuilder = (instrType: InstrumentType, partNumber: Int) => {
      val instrument = new JFugueInstrument(instrType)
      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(new MIDIReaderComposer(filename, partNumber))
    }

    for ((instrument, parts) <- parser.getPartIndexByInstrument) {
      parts
        .map(musicianBuilder(instrument, _).withActorSystem(orchestra.system))
        .foreach(m => orchestra.registerMusician(m.build))
    }

    orchestra.start()
  }
}

object DemoRandomOrchestra {
  def run() = {
    val orchestra = new Orchestra()
    //val instrSet = Set(PIANO, PIANO, KICK)
    val instrSet = Set(PERCUSSIVE(30))

    val musicianBuilder = (instrType: InstrumentType) => {
      val instrument = new OvertoneInstrument(instrumentType = instrType)
      AIMusician.builder
        .withInstrument(instrument)
    }

    instrSet
      .map(t => musicianBuilder(t).withActorSystem(orchestra.system))
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()

    orchestra.shutdown(10000L)
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
