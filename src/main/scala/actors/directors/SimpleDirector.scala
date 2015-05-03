package actors.directors

import akka.actor.{ActorLogging, ActorSystem, Cancellable}
import messages.{Stop, Start, MusicInfoMessage, SyncMessage}
import utils.ActorUtils
import utils.ImplicitConversions._
import utils.builders.{Count, IsOnce, Once, Zero}

import scala.collection.mutable

case class SimpleDirectorBuilder[ActorSysCount <: Count](
                                  var actorSystem: Option[ActorSystem] = None,
                                  var syncFrequencyMS: Option[Long] = None) extends DirectorBuilder[ActorSysCount] {
    override def withActorSystem(actorSystem: Option[ActorSystem]) = copy[Once](actorSystem = actorSystem)
    def withSyncFrequencyMS(syncFrequencyMS: Option[Long]) = copy[ActorSysCount](syncFrequencyMS = syncFrequencyMS)

    override def build[A <: ActorSysCount : IsOnce]: Director = new SimpleDirector(this.asInstanceOf[SimpleDirectorBuilder[Once]])
}

object SimpleDirector {
  def builder = new SimpleDirectorBuilder[Zero]()
  val DEFAULT_SYNC_FREQ_MS: Long = 200

  /**
   * Amount of time in MS approximately expected before some message from the musicians should have been received
   */
  val TIMEOUT: Long = 5000
}

class SimpleDirector(builder: SimpleDirectorBuilder[Once]) extends Director with ActorLogging {
  val actorSystem: ActorSystem = builder.actorSystem.get
  val syncFrequencyMS: Long = builder.syncFrequencyMS.getOrElse(SimpleDirector.DEFAULT_SYNC_FREQ_MS)

  private var task: Option[Cancellable] = None
  private var tickCount: Long = 0

  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = //TODO: Consider using a cache (http://spray.io/documentation/1.2.3/spray-caching/)
    new mutable.HashMap[Long, mutable.Set[MusicInfoMessage]]() with mutable.MultiMap[Long, MusicInfoMessage]

  override def start(): Unit = {
    task = ActorUtils.schedule(
      actorSystem,
      delayMS = 0,
      intervalMS = syncFrequencyMS,
      task = () => sync())
  }

  override def stop(): Unit = {
    task.foreach(_.cancel())
  }

  def isSystemHealthy: Boolean = {
    val ticksBeforeTimeout = (SimpleDirector.TIMEOUT / SimpleDirector.DEFAULT_SYNC_FREQ_MS).toInt
    tickCount < ticksBeforeTimeout ||
    (1 to ticksBeforeTimeout).exists{ delta =>
      musicInfoMessageCache.get(tickCount - delta).exists{ messages =>
        messages.exists(message => ! message.musicalElement.isEmpty)
      }
    }
  }

  def sync(): Unit = {
    if (! isSystemHealthy) {
      stop()
      actorSystem.shutdown()
    } else {
      ActorUtils.broadcast(actorSystem, SyncMessage(tickCount))
      tickCount += 1
    }
  }

  override def receive: Receive = {
    case m: MusicInfoMessage =>
      log.debug(s"Received: $m")
      musicInfoMessageCache.addBinding(m.time, m)
    case Start =>
      start()
    case Stop =>
      stop()
  }
}

