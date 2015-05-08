package actors.monitors

import akka.actor.{ActorSystem, ActorRef}
import utils.builders._

// Type-safe Builders are not feasible through inheritance: As soon as I have more than one implementation
// I'll have to give up either
trait HealthMonitorFactory[A <: Count] {
  def withTickFrequency(tickFrequency: Long): HealthMonitorFactory[Once]
  def build[B <: A : IsOnce]: HealthMonitor
  def buildAsActor[B <: A : IsOnce](implicit system: ActorSystem): ActorRef
}

trait HealthMonitor extends Monitor {
  def receivedHeartbeat(time: Long, actor: ActorRef)
  def isSystemHealthy: Boolean
}
