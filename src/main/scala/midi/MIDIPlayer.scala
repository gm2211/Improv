package midi

import javax.sound.midi.{MetaEventListener, MetaMessage, MidiSystem, Sequence}

import designPatterns.observer.{EventNotification, Observable}
import midi.MIDIPlayer.FinishedPlaying
import utils.ImplicitConversions.anyToRunnable

object MIDIPlayer {

  case object FinishedPlaying extends EventNotification

  val END_OF_SEQUENCE = 47
}

class MIDIPlayer extends Observable with MetaEventListener {
  val sequencer = MidiSystem.getSequencer
  var playing = false

  override def meta(meta: MetaMessage): Unit = {
    if (meta.getType == MIDIPlayer.END_OF_SEQUENCE) {
      sequencer.stop()
      sequencer.close()
      playing = false
      notifyObservers(FinishedPlaying)
    }
  }

  Runtime.getRuntime.addShutdownHook(new Thread(() => sequencer.close()))

  def play(sequence: Sequence): Unit = {
    if (!sequencer.isOpen)
      sequencer.open()

    sequencer.setSequence(sequence)
    sequencer.addMetaEventListener(this)

    sequencer.start()
    playing = true
  }

  def stop() = {
    if (sequencer.isOpen) {
      sequencer.stop()
      sequencer.removeMetaEventListener(this)
    }
    playing = false
  }
}
