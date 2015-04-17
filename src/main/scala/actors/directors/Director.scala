package actors.directors

trait Director {
  def start(): Unit
  def stop(): Unit
}
