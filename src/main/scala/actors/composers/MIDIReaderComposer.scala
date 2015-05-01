package actors.composers

import midi.MIDIParser
import representation.Phrase

import scala.util.Try

class MIDIReaderComposer(val filename: String, val partNum: Int) extends Composer {
  val midiReader = MIDIParser(filename)
  var phraseIterOpt: Option[Iterator[Option[Phrase]]] = Try(midiReader.getPhrases(partNum)).toOption

  override def compose(previousPhrase: Phrase): Phrase = {
    phraseIterOpt.flatMap(_.next())
      .getOrElse(Phrase.builder.build())
  }
}
