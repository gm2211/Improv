package actors.monitors

class SimpleStatsMonitor extends StatsMonitor {
  override def activeActorsCount: Int = 0 //TODO: Provide a real implementation
  override def reset(): Unit = () //TODO: Implement this
}
