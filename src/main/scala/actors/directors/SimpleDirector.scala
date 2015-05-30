package actors.directors

import actors.monitors.{HealthMonitor, HealthMonitorFactory, SimpleHealthMonitor}
import akka.actor.{ActorLogging, ActorSystem, Cancellable}
import messages.{MusicInfoMessage, Start, Stop, SyncMessage}
import utils.ActorUtils
import utils.ImplicitConversions.anyToRunnable
import utils.builders.{Count, IsAtLeastOnce, AtLeastOnce, Zero}

case class SimpleDirectorBuilder[ActorSysCount <: Count](
    var actorSystem: Option[ActorSystem] = None,
    var syncFrequencyMS: Option[Long] = None,
    var healthMonitorFactory: Option[HealthMonitorFactory[Zero]] = None) extends DirectorBuilder[ActorSysCount] {

  override def withActorSystem(actorSystem: ActorSystem) =
    copy[AtLeastOnce](actorSystem = Some(actorSystem))

  def withSyncFrequencyMS(syncFrequencyMS: Long) =
    copy[ActorSysCount](syncFrequencyMS = Some(syncFrequencyMS))

  def withHealthMonitor(healthMonitorFactory: HealthMonitorFactory[Zero]) =
    copy[ActorSysCount](healthMonitorFactory = Some(healthMonitorFactory))

  override def build[A <: ActorSysCount : IsAtLeastOnce]: Director =
    new SimpleDirector(this.asInstanceOf[SimpleDirectorBuilder[AtLeastOnce]])
}

object SimpleDirector {
  def builder = new SimpleDirectorBuilder[Zero]()

  val DEFAULT_SYNC_FREQ_MS: Long = 200
}

class SimpleDirector(builder: SimpleDirectorBuilder[AtLeastOnce]) extends Director with ActorLogging {
  implicit val actorSystem: ActorSystem = builder.actorSystem.get
  val syncFrequencyMS: Long = builder.syncFrequencyMS.getOrElse(SimpleDirector.DEFAULT_SYNC_FREQ_MS)

  private var task: Option[Cancellable] = None
  private var tickCount: Long = 0
  private val healthMonitor: HealthMonitor = {
    builder.healthMonitorFactory.getOrElse(SimpleHealthMonitor.builder).withTickFrequency(syncFrequencyMS).build
  }


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

