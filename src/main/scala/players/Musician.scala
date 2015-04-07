package players

import java.util.UUID

import akka.actor.{Actor, ActorRef, ActorSystem}
import instruments.OvertoneInstrumentType.OvertoneInstrumentType
import instruments.{Instrument, OvertoneInstrument}
import overtone.wrapper.OvertoneWrapper
import representation.Note

import scala.language.implicitConversions

object Musician {
  implicit def createActorSystem(name: String = "actorSystem"): ActorSystem = ActorSystem(name)

  def createAIMusicianWithInstrument(actorSystem: ActorSystem,
                       instrument: Option[Instrument],
                       name: String): ActorRef = {
    return actorSystem.actorOf(AIMusician.props(instrument), name)
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

trait Musician extends Actor {
  def play(note: Note): Unit
}
