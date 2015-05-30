package actors.musicians

import actors.composers.{Composer, RandomComposer}
import actors.musicians.behaviour.{ReceiveBehaviour, AIMusicianBehaviour, ActorBehaviour}
import akka.actor.{ActorLogging, ActorSystem, Props}
import instruments.Instrument
import instruments.InstrumentType.InstrumentType
import messages.{Message, MusicInfoMessage, SyncMessage}
import representation.Phrase
import utils.ActorUtils
import utils.builders.{Count, IsAtLeastOnce, AtLeastOnce, Zero}
import utils.collections.CollectionUtils

import scala.collection.mutable

case class AIMusicianBuilder
[InstrumentCount <: Count,
 BehavioursCount <: Count,
 ActorSysCount <: Count](
  var instrument: Option[Instrument] = None,
  var behaviours: Option[List[ActorBehaviour]] = None,
  var actorSystem: Option[ActorSystem] = None,
  var composer: Option[Composer] = None,
  var messageOnly: Option[Boolean] = Some(false)) {

  def withInstrument(instrument: Instrument) =
    copy[AtLeastOnce, BehavioursCount, ActorSysCount](instrument = Some(instrument))
  
  def addBehaviour(behaviour: ActorBehaviour) =
    copy[InstrumentCount, AtLeastOnce, ActorSysCount](behaviours = Some(behaviours.toList.flatten :+ behaviour))

  def withBehaviours(behaviours: List[ActorBehaviour]) =
    copy[InstrumentCount, AtLeastOnce, ActorSysCount](behaviours = Some(behaviours))

  def withActorSystem(actorSystem: ActorSystem) =
    copy[InstrumentCount, BehavioursCount, AtLeastOnce](actorSystem = Some(actorSystem))

  def withComposer(composer: Composer) =
    copy[InstrumentCount, BehavioursCount, ActorSysCount](composer = Some(composer))

  def isMessageOnly =
    copy[InstrumentCount, BehavioursCount, ActorSysCount](messageOnly = Some(true))

  def build[
  A <: InstrumentCount : IsAtLeastOnce,
  B <: BehavioursCount : IsAtLeastOnce,
  C <: ActorSysCount : IsAtLeastOnce]: AIMusician = {
    new AIMusician(this.asInstanceOf[AIMusicianBuilder[AtLeastOnce, AtLeastOnce, AtLeastOnce]])
  }

  def buildProps[
  A <: InstrumentCount : IsAtLeastOnce,
  B <: BehavioursCount : IsAtLeastOnce,
  C <: ActorSysCount : IsAtLeastOnce]: Props = Props(build)
}

object AIMusician {
  def builder: AIMusicianBuilder[Zero, Zero, Zero] = new AIMusicianBuilder[Zero, Zero, Zero]
}

class AIMusician(builder: AIMusicianBuilder[AtLeastOnce, AtLeastOnce, AtLeastOnce]) extends Musician with ActorLogging {
  private val instrument: Instrument = builder.instrument.get
  private val behaviours: List[ActorBehaviour] = builder.behaviours.get
  implicit private val actorSystem: ActorSystem = builder.actorSystem.get
  private val musicComposer: Composer = builder.composer.getOrElse(new RandomComposer)
  private val messageOnly: Boolean = builder.messageOnly.get

  private[musicians] val musicInfoMessageCache = CollectionUtils.createHashMultimap[Long, MusicInfoMessage]
  private[musicians] var currentMusicTime: Long = 0

  behaviours.foreach{ case b: AIMusicianBehaviour => b.registerMusician(this) case _ => }

  def play(time: Long): Unit = {
    val instrumentsAndPhrases: Traversable[(InstrumentType, Phrase)] = musicInfoMessageCache.get(time)
      .map(_.map(m => (m.instrument, m.phrase))).getOrElse(Set())

    musicInfoMessageCache.remove(time)

    val responsePhrase = musicComposer.compose(instrumentsAndPhrases)
    responsePhrase.foreach(play)
  }

  override def play(phrase: Phrase): Unit = {
    if (!messageOnly) instrument.play(phrase)

    ActorUtils.broadcast(
      MusicInfoMessage(
        phrase,
        currentMusicTime,
        instrument.instrumentType))
  }

  override def receive = {
    case m: Message =>
      behaviours.foreach{
        case (behaviour: ReceiveBehaviour) => behaviour(m)
        case _ =>
      }
  }
}
