package utils

object ImplicitConversions {
  implicit def anyToRunnable[F](f: () => F): Runnable = new Runnable {
    override def run(): Unit = f()
  }
}
