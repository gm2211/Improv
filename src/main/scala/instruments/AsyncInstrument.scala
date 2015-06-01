package instruments

import designPatterns.observer.{EventNotification, Observable}

object AsyncInstrument {
  case object FinishedPlaying extends EventNotification
}
trait AsyncInstrument extends Instrument with Observable {
}
