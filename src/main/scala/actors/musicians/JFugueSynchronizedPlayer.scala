package actors.musicians

import instruments.JFugueUtils
import messages.{MusicInfoMessage, SyncMessage}
import org.jfugue.pattern.Pattern
import org.jfugue.player.Player
import representation.MusicalElement
import utils.CollectionUtils

import scala.collection.mutable

/**
 * This musician listens to all the music messages sent by other musicians and plays them all at the same time on the
 * next sync message (this kind of acts like a sync barrier)
 */
class JFugueSynchronizedPlayer extends Musician {
  //TODO: Consider using a cache (http://spray.io/documentation/1.2.3/spray-caching/)
  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = CollectionUtils.createHashMultimap

  def merge(messages: mutable.Set[MusicInfoMessage]): Pattern = {
    val instrElemSet = messages
      .map(message => (message.instrument, message.musicalElement))
      .toSet
    JFugueUtils.createMultiVoicePattern(instrElemSet)
  }

  override def receive: Receive = {
    case musicMessage: MusicInfoMessage =>
      musicInfoMessageCache.addBinding(musicMessage.time, musicMessage)
    case syncMessage: SyncMessage =>
      musicInfoMessageCache.get(syncMessage.time - 1)
        .map(merge)
        .exists { pattern => println(pattern); new Player().play(pattern); true }
      musicInfoMessageCache.remove(syncMessage.time - 1)
  }

  override def play(musicalElement: MusicalElement): Unit = ()
}
