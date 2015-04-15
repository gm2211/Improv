package players

trait Director extends Runnable {
  def sync(): Unit
  override def run(): Unit = sync()
}
