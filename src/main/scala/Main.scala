import actors.Orchestra
import actors.composers.MIDIReaderComposer
import actors.musicians.AIMusician
import instruments.InstrumentType._
import instruments.JFugueInstrument
import midi.{MIDIInstrumentCategory => MidiType}
import utils.ImplicitConversions.wrapInOption

object Main extends App {
  //MIDIParser("/Users/gm2211/Documents/imperialCollege/fourthYear/finalProject/codebase/src/main/resources/musicScores/test.mid").getInstrumentsCounts
  DemoOrchestra.run()
}

object DemoOrchestra {
  def run() = {
    val orchestra = new Orchestra()
    val instrSet = Set((PIANO, MidiType.PIANO(0)), (PIANO, MidiType.PIANO(7)), (KICK, MidiType.CHROMATIC_PERCUSSION(11)))

    val musicianBuilder = (instrType: InstrumentType, instrCat: MidiType.MIDIInstrumentCategory) => {
      val instrument = new JFugueInstrument(instrType)
      AIMusician.builder
        .withInstrument(instrument)
        .withComposer(new MIDIReaderComposer(getClass.getClassLoader.getResource("musicScores/test.mid").getPath, instrCat))
    }

    instrSet
      .map{case (t, c) => musicianBuilder(t,c).withActorSystem(orchestra.system)}
      .foreach(m => orchestra.registerMusician(m.build))

    orchestra.start()

    //orchestra.shutdown(10000L)
  }
}
