package actors.composers

import midi.JMusicMIDIParser
import representation.Phrase

import scala.util.Try

class MIDIReaderComposer(val filename: String, val partNum: Int) extends Composer {
  val midiReader = JMusicMIDIParser(filename)
  var phraseIterOpt: Option[Iterator[Option[Phrase]]] = Try(midiReader.getPhrases(partNum)).toOption

  override def compose(previousPhrase: Phrase): Phrase = {
    phraseIterOpt.flatMap(_.next())
      .getOrElse(Phrase.builder.build())
  }
}
