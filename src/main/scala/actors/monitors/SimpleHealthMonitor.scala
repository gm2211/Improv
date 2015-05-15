package actors.monitors

import akka.actor._
import messages.MusicInfoMessage
import utils.builders.{Count, IsOnce, Once, Zero}
import utils.collections.MultiCache

case class SimpleHealthMonitorBuilder[TickFrequencyCount <: Count]
  (
    tickFrequency: Option[Long] = None,
    timeoutMS: Option[Long] = None,
    cacheSize: Option[Long] = None,
    statsMonitor: Option[StatsMonitor] = None) extends HealthMonitorFactory[TickFrequencyCount] {

  def withTickFrequency(tickFrequency: Long) = copy[Once](tickFrequency = Some(tickFrequency))

  def withTimeout(timeoutMS: Long) = copy[TickFrequencyCount](timeoutMS = Some(timeoutMS))

  def withCacheSize(cacheSize: Long) = copy[TickFrequencyCount](cacheSize = Some(cacheSize))

  def withStatsMonitor(statsMonitor: StatsMonitor) =  copy[TickFrequencyCount](statsMonitor = Some(statsMonitor))

  def build[A <: TickFrequencyCount : IsOnce]: SimpleHealthMonitor = new SimpleHealthMonitor(this.asInstanceOf[SimpleHealthMonitorBuilder[Once]])

  override def buildAsActor[A <: TickFrequencyCount : IsOnce](implicit system: ActorSystem): ActorRef = {
    val props = Props(new SimpleHealthMonitor(this.asInstanceOf[SimpleHealthMonitorBuilder[Once]]) with Actor {
        override def receive: Actor.Receive = {
          case m: MusicInfoMessage =>
            receivedHeartbeat(m.time, sender())
        }
      })
    system.actorOf(props)
  }
}

object SimpleHealthMonitor {
  def builder = new SimpleHealthMonitorBuilder[Zero]

  val DEFAULT_CACHE_SIZE = 1000
  val DEFAULT_TIMEOUT_MS: Long = 5000
}

class SimpleHealthMonitor(builder: SimpleHealthMonitorBuilder[Once]) extends HealthMonitor {
  val tickFrequency = builder.tickFrequency.get
  val timeoutMS = builder.timeoutMS.getOrElse(SimpleHealthMonitor.DEFAULT_TIMEOUT_MS)
  val cacheSize: Long = builder.cacheSize.getOrElse(SimpleHealthMonitor.DEFAULT_CACHE_SIZE)
  private val statsMonitor: StatsMonitor = builder.statsMonitor.getOrElse(new SimpleStatsMonitor)
  private val heartbeatsCache: MultiCache[java.lang.Long, ActorPath] =  MultiCache.buildDefault(timeoutMS, cacheSize)
  private var lastActivity: Long = 0

  override def isSystemHealthy: Boolean = {
    true // TODO: Provide a reasonable implementation
  }

  override def receivedHeartbeat(time: Long, actor: ActorRef): Unit = {
    heartbeatsCache.addBinding(time, actor.path)
    lastActivity = time
  }
}
