import akka.actor.ActorSystem
import instruments.Instrument
import messages.SyncMessage
import players.AIMusician

object Main {
  def main(args: Array[String]): Unit = {
    val instrument = new Instrument {
      override def play(): Unit = println("Play")
    }
    val orchestra = ActorSystem("orchestra")
    val musician = orchestra.actorOf(AIMusician.props(instrument), "MyMusician")
    musician ! new SyncMessage
  }
}
