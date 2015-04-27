package actors.composers

import midi.MIDIInstrumentCategory.MIDIInstrumentCategory
import midi.MIDIParser
import representation.Phrase
import utils.CollectionUtils

class MIDIReaderComposer(val filename: String, val instrumentType: MIDIInstrumentCategory) extends Composer {
  val midiReader = MIDIParser(filename)
  val partNum = {
    val partIndices = midiReader.getPartIndexByInstrument.getOrElse(instrumentType, new Array[Int](0))
    CollectionUtils.chooseRandom(partIndices)
  }

  var phraseIterOpt: Option[Iterator[Option[Phrase]]] = {
    for (pNum <- partNum)
      yield midiReader.getPhrases(pNum)
  }

  override def compose(previousPhrase: Phrase): Phrase = {
    var phrase: Option[Phrase] = None
    if (phraseIterOpt.isDefined && phraseIterOpt.get.hasNext)
        phrase = phraseIterOpt.get.next()
    phrase.getOrElse(Phrase.builder.build())
  }
}
