package actors.musicians

import actors.composers.{Composer, RandomComposer}
import akka.actor.{ActorLogging, ActorSystem, Props}
import instruments.Instrument
import instruments.InstrumentType.InstrumentType
import messages.{MusicInfoMessage, SyncMessage}
import representation.Phrase
import utils.ActorUtils
import utils.builders.{Count, IsOnce, Once, Zero}
import utils.collections.CollectionUtils

import scala.collection.mutable

case class AIMusicianBuilder
[InstrumentCount <: Count,
ActorSysCount <: Count](
  var instrument: Option[Instrument] = None,
  var actorSystem: Option[ActorSystem] = None,
  var composer: Option[Composer] = None,
  var messageOnly: Option[Boolean] = Some(false)) {
  def withInstrument(instrument: Instrument) = copy[Once, ActorSysCount](instrument = Some(instrument))

  def withActorSystem(actorSystem: ActorSystem) = copy[InstrumentCount, Once](actorSystem = Some(actorSystem))

  def withComposer(composer: Composer) = copy[InstrumentCount, ActorSysCount](composer = Some(composer))

  def isMessageOnly = copy[InstrumentCount, ActorSysCount](messageOnly = Some(true))

  def build[
  A <: InstrumentCount : IsOnce,
  B <: ActorSysCount : IsOnce]: AIMusician = {
    new AIMusician(this.asInstanceOf[AIMusicianBuilder[Once, Once]])
  }

  def buildProps[
  A <: InstrumentCount : IsOnce,
  B <: ActorSysCount : IsOnce]: Props = Props(build)
}

object AIMusician {
  def builder: AIMusicianBuilder[Zero, Zero] = new AIMusicianBuilder[Zero, Zero]
}

class AIMusician(builder: AIMusicianBuilder[Once, Once]) extends Musician with ActorLogging {
  private val instrument: Instrument = builder.instrument.get
  implicit private val actorSystem: ActorSystem = builder.actorSystem.get
  private val musicComposer: Composer = builder.composer.getOrElse(new RandomComposer)
  private val messageOnly: Boolean = builder.messageOnly.get

  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = CollectionUtils.createHashMultimap
  private var currentMusicTime: Long = 0

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
    case m: SyncMessage =>
      currentMusicTime = m.time
      play(m.time)

    case m: MusicInfoMessage =>
      musicInfoMessageCache.addBinding(m.time, m)
  }
}
