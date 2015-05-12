package actors.musicians

import java.util.concurrent.{ThreadPoolExecutor, Executors}

import instruments.JFugueUtils
import messages.{MusicInfoMessage, SyncMessage}
import org.jfugue.player.Player
import representation.MusicalElement
import utils.collections.MultiCache

import scala.collection.mutable
import utils.ImplicitConversions.anyToRunnable

/**
 * This musician listens to all the music messages sent by other musicians and plays them all at the same time on the
 * next sync message (this kind of acts like a sync barrier)
 */
class JFugueSynchronizedPlayer extends Musician {
  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = MultiCache.buildDefault(5000, 50)

  private def prepare(messages: mutable.Set[MusicInfoMessage]): String = {
    JFugueUtils.mergePatterns(messages.map(message => JFugueUtils.createPattern(message.musicalElement, message.instrument.instrumentNumber)))
  }

  override def receive: Receive = {
    case musicMessage: MusicInfoMessage =>
      musicInfoMessageCache.addBinding(musicMessage.time, musicMessage)
    case syncMessage: SyncMessage =>
      val pattern = musicInfoMessageCache.get(syncMessage.time - 1)
        .map(prepare).getOrElse("")

      new Player().play(pattern)

      musicInfoMessageCache.remove(syncMessage.time - 1)
  }

  override def play(musicalElement: MusicalElement): Unit = ()
}
