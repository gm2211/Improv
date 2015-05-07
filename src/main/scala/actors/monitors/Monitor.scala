package actors.monitors

import akka.actor.{ActorRef, ActorSystem}

trait MonitorFactory {
  def buildAsActor(implicit system: ActorSystem): ActorRef
}

trait Monitor {
}
