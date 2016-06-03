package actors.monitors

trait StatsMonitor extends Monitor {
  def activeActorsCount: Int
  def reset(): Unit
}
