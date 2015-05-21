package actors.musicians

import instruments.JFugueUtils
import messages.{MusicInfoMessage, SyncMessage}
import org.jfugue.player.Player
import representation.MusicalElement
import utils.collections.MultiCache
import utils.ImplicitConversions.anyToRunnable

import scala.collection.mutable

/**
 * This musician listens to all the music messages sent by other musicians and plays them all at the same time on the
 * next sync message (this kind of acts like a sync barrier)
 */
class JFugueSynchronizedPlayer extends Musician {
  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = MultiCache.buildDefault(5000, 50)
  val player: Player = new Player()

  private def prepare(messages: mutable.Set[MusicInfoMessage]): String = {
    val pattern = JFugueUtils.mergePatterns(
      messages.map(message =>
        JFugueUtils.createPattern(message.musicalElement, message.instrument.instrumentNumber))
    )

    pattern
  }

  override def receive: Receive = {
    case musicMessage: MusicInfoMessage =>
      musicInfoMessageCache.addBinding(musicMessage.time, musicMessage)
    case syncMessage: SyncMessage =>
      val pattern = musicInfoMessageCache.get(syncMessage.time - 1)
        .map(prepare).getOrElse("")

      new Thread(() => player.play(pattern)).start()

      musicInfoMessageCache.remove(syncMessage.time - 1)
  }

  override def play(musicalElement: MusicalElement): Unit = ()
}
