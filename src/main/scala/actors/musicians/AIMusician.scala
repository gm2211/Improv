package actors.musicians

import java.util.concurrent.Executors

import actors.composers.{Composer, RandomComposer}
import actors.musicians.behaviour._
import akka.actor.{ActorLogging, ActorRef, ActorSystem, Props}
import designPatterns.observer.{EventNotification, Observer}
import instruments.InstrumentType.InstrumentType
import instruments.{AsyncInstrument, Instrument}
import messages.{FinishedPlaying, DirectorIdentityInfoMessage, Message, MusicInfoMessage}
import representation.Phrase
import utils.ActorUtils
import utils.ImplicitConversions.{toEnhancedIterable, anyToRunnable}
import utils.builders.{AtLeastOnce, Count, IsAtLeastOnce, Zero}
import utils.collections.CollectionUtils
import utils.functional.FunctionalUtils

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
  def getDefaultBehaviours: List[ActorBehaviour] = {
    List(
      new SyncMessageReceivedBehaviour,
      new MusicMessageInfoReceivedBehaviour
    )
  }

  def builder: AIMusicianBuilder[Zero, Zero, Zero] = new AIMusicianBuilder[Zero, Zero, Zero]
}

class AIMusician(builder: AIMusicianBuilder[AtLeastOnce, AtLeastOnce, AtLeastOnce])
    extends Musician with ActorLogging with Observer {
  private val instrument: Instrument = builder.instrument.get
  private val behaviours: List[ActorBehaviour] = builder.behaviours.get
  private val receiveBehaviours = FunctionalUtils.combine(behaviours.filterByType[ReceiveBehaviour])
  implicit private val actorSystem: ActorSystem = builder.actorSystem.get
  private val musicComposer: Composer = builder.composer.getOrElse(new RandomComposer)
  private val messageOnly: Boolean = builder.messageOnly.get
  private var director: Option[ActorRef] = None

  instrument match {
    case instr: AsyncInstrument =>
      instr.addObserver(this)
    case _ =>
  }

  private[musicians] val musicInfoMessageCache = CollectionUtils.createHashMultimap[Long, MusicInfoMessage]
  private[musicians] var currentMusicTime: Long = 0

  behaviours.foreach{ case b: AIMusicianBehaviour => b.registerMusician(this) case _ => }

  def play(time: Long): Unit = {
    val instrumentsAndPhrases: Traversable[(InstrumentType, Phrase)] = musicInfoMessageCache.get(time)
      .map(_.map(m => (m.instrument, m.phrase))).getOrElse(Set())

    musicInfoMessageCache.remove(time)

    val responsePhrase = musicComposer.compose(instrumentsAndPhrases)
    log.debug("playing")
    responsePhrase.foreach(play)
  }

  override def play(phrase: Phrase): Unit = {
    if (!messageOnly) instrument.play(phrase)

    ActorUtils.broadcast(
      MusicInfoMessage(
        phrase,
        currentMusicTime,
        instrument.instrumentType,
        self,
        director))
  }

  override def receive = {
    case DirectorIdentityInfoMessage(_, director_) =>
      log.debug(s"${self.path.name} got message from director")
      this.director = Some(director_)
    case m: Message =>
      receiveBehaviours(m)
  }

  override def notify(eventNotification: EventNotification): Unit = eventNotification match {
    case AsyncInstrument.FinishedPlaying =>
      log.debug(s"${self.path.name} sending finished playing to ${director.map(_.path.name)}")
      director.foreach(_ ! FinishedPlaying(self))
  }
}
