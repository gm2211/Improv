package utils

import instruments.OvertoneInstrumentType._
import overtone.wrapper.OvertoneWrapper
import representation.Note

object OvertoneUtils {
  private def getInstPackage(instrument: OvertoneInstrumentType) = instrument match {
    case PIANO =>
      "piano"
    case SAMPLED_PIANO =>
      "sampled-piano"
    case KICK | KICK2 | KICK3 | KICK4 | DRY_KICK | DUB_KICK | SNARE | CLAP =>
      "drum"
    case PING =>
      "synth"
  }

  def useInstrument(instrument: OvertoneInstrumentType): String = {
    s"(use 'overtone.inst.${getInstPackage(instrument)})"
  }

  def useInstrument(instrument: OvertoneInstrumentType, wrapper: OvertoneWrapper): Unit = {
    wrapper.sendCommand(useInstrument(instrument))
  }

  def note(note: Note): String = f"(note :${note.name}%s${note.octave}%d)"

  def play(instrument: OvertoneInstrumentType, note: Note, wrapper: OvertoneWrapper): Unit = {
    wrapper.sendCommand(f"($instrument%s ${this.note(note)})")
  }
}
