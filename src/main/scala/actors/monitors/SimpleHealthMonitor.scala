package actors.monitors

import java.util.concurrent.TimeUnit

import akka.actor._
import com.google.common.cache.{Cache, CacheBuilder}
import messages.MusicInfoMessage

import scala.collection.JavaConversions._

case class SimpleHealthMonitorBuilder(
    timeoutMS: Option[Long] = None,
    cacheSize: Option[Long] = None,
    statsMonitor: Option[StatsMonitor] = None) extends MonitorFactory {

  def withTimeout(timeoutMS: Long) = copy(timeoutMS = Some(timeoutMS))

  def withCacheSize(cacheSize: Long) = copy(cacheSize = Some(cacheSize))

  def withStatsMonitor(statsMonitor: StatsMonitor) = copy(statsMonitor = Some(statsMonitor))

  def build: SimpleHealthMonitor = new SimpleHealthMonitor(this)

  override def buildAsActor(implicit system: ActorSystem): ActorRef = {
    val props = Props(new SimpleHealthMonitor(this) with Actor {
        override def receive: Actor.Receive = {
          case m: MusicInfoMessage =>
            receivedHeartbeat(m.time, sender())
        }
      })
    system.actorOf(props)
  }
}

object SimpleHealthMonitor {
  def builder: SimpleHealthMonitorBuilder = new SimpleHealthMonitorBuilder

  val DEFAULT_CACHE_SIZE = 1000
  val DEFAULT_TIMEOUT_MS: Long = 5000
}

class SimpleHealthMonitor(builder: SimpleHealthMonitorBuilder) extends HealthMonitor {
  val timeoutMS = builder.timeoutMS.getOrElse(SimpleHealthMonitor.DEFAULT_TIMEOUT_MS)
  val cacheSize: Long = builder.cacheSize.getOrElse(SimpleHealthMonitor.DEFAULT_CACHE_SIZE)
  private val statsMonitor: StatsMonitor = builder.statsMonitor.getOrElse(new SimpleStatsMonitor)
  private val heartbeatsCache: Cache[java.lang.Long, ActorPath] =  {
    CacheBuilder.newBuilder()
      .expireAfterAccess(timeoutMS, TimeUnit.MILLISECONDS)
      .maximumSize(cacheSize)
      .build[java.lang.Long, ActorPath]()
  }

  override def isSystemHealthy: Boolean = {
    //heartbeatsCache.getIfPresent(lastActivityTime)
    true
  }

  override def receivedHeartbeat(time: Long, actor: ActorRef): Unit =
    heartbeatsCache.put(time, actor.path)

  private def lastActivityTime: Long = heartbeatsCache.asMap().keySet().max

}
