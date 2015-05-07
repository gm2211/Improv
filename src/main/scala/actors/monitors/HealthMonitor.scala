package actors.monitors

import akka.actor.ActorRef

trait HealthMonitor extends Monitor {
  def receivedHeartbeat(time: Long, actor: ActorRef)
  def isSystemHealthy: Boolean
}
