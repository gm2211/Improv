package actors.directors

import actors.monitors.{HealthMonitor, SimpleHealthMonitor}
import akka.actor.{ActorLogging, ActorSystem, Cancellable}
import messages.{MusicInfoMessage, Start, Stop, SyncMessage}
import utils.ActorUtils
import utils.ImplicitConversions.anyToRunnable
import utils.builders.{Count, IsOnce, Once, Zero}

case class SimpleDirectorBuilder[ActorSysCount <: Count](
    var actorSystem: Option[ActorSystem] = None,
    var syncFrequencyMS: Option[Long] = None,
    var healthMonitor: Option[HealthMonitor] = None) extends DirectorBuilder[ActorSysCount] {

  override def withActorSystem(actorSystem: ActorSystem) = copy[Once](actorSystem = Some(actorSystem))

  def withSyncFrequencyMS(syncFrequencyMS: Long) = copy[ActorSysCount](syncFrequencyMS = Some(syncFrequencyMS))

  def withHealthMonitor(healthMonitor: HealthMonitor) = copy[ActorSysCount](healthMonitor = Some(healthMonitor))

  override def build[A <: ActorSysCount : IsOnce]: Director = new SimpleDirector(this.asInstanceOf[SimpleDirectorBuilder[Once]])
}

object SimpleDirector {
  def builder = new SimpleDirectorBuilder[Zero]()

  val DEFAULT_SYNC_FREQ_MS: Long = 200
}

class SimpleDirector(builder: SimpleDirectorBuilder[Once]) extends Director with ActorLogging {
  implicit val actorSystem: ActorSystem = builder.actorSystem.get
  val syncFrequencyMS: Long = builder.syncFrequencyMS.getOrElse(SimpleDirector.DEFAULT_SYNC_FREQ_MS)

  private var task: Option[Cancellable] = None
  private var tickCount: Long = 0
  private val healthMonitor: HealthMonitor = builder.healthMonitor.getOrElse(SimpleHealthMonitor.builder.build)


  override def start(): Unit = {
    task = ActorUtils.schedule(
      delayMS = 0,
      intervalMS = syncFrequencyMS,
      task = () => sync())
  }

  override def stop(): Unit = {
    task.foreach(_.cancel())
  }

  def sync(): Unit = {
    if (!healthMonitor.isSystemHealthy) {
      stop()
      actorSystem.shutdown()
    } else {
      ActorUtils.broadcast(SyncMessage(tickCount))
      tickCount += 1
    }
  }

  override def receive: Receive = {
    case m: MusicInfoMessage =>
      healthMonitor.receivedHeartbeat(m.time, sender())
    case Start =>
      start()
    case Stop =>
      stop()
  }
}

