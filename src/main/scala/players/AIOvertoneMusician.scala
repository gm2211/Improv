package players

import java.util.UUID

import akka.actor.{ActorRef, ActorSystem, Props}
import instruments.OvertoneInstrumentType.OvertoneInstrumentType
import instruments.{Instrument, OvertoneInstrument}
import overtone.wrapper.OvertoneWrapper

import scala.language.implicitConversions


object AIOvertoneMusician {
  implicit def createActorSystem(name: String = "actorSystem"): ActorSystem = ActorSystem(name)

  def props(instrument: Instrument, composer: Option[Composer] = None): Props =
    Props(new AIMusician(instrument, composer))

  def createAIMusicianWithInstrument(actorSystem: ActorSystem,
                                     instrument: Option[Instrument],
                                     name: String): ActorRef = {
    return actorSystem.actorOf(AIOvertoneMusician.props(instrument.getOrElse(new OvertoneInstrument)), name)
  }

  def createAIMusician(actorSystem: ActorSystem,
                       name: String = UUID.randomUUID().toString): ActorRef = {
    return createAIMusicianWithInstrument(actorSystem, None, name)
  }

  def createAIMusicianWithInstrType(actorSystem: ActorSystem,
                                    instrumentType: OvertoneInstrumentType,
                                    name: String = UUID.randomUUID().toString): ActorRef = {
    val overtoneWrapper = new OvertoneWrapper()
    val instrument = new OvertoneInstrument(overtoneWrapper, Some(instrumentType))
    return createAIMusicianWithInstrument(actorSystem, Some(instrument), name)
  }
}


