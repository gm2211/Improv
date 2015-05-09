package actors.musicians

import actors.composers.{Composer, RandomComposer}
import akka.actor.{ActorLogging, ActorSystem, Props}
import instruments.Instrument
import messages.{MusicInfoMessage, SyncMessage}
import representation.{MusicalElement, Phrase}
import utils.builders.{Count, IsOnce, Once, Zero}
import utils.ActorUtils
import utils.collections.CollectionUtils

import scala.collection.mutable

case class AIMusicianBuilder
[InstrumentCount <: Count,
ActorSysCount <: Count](
  var instrument: Option[Instrument] = None,
  var actorSystem: Option[ActorSystem] = None,
  var composer: Option[Composer] = None,
  var messageOnly: Option[Boolean] = Some(false)) {
  def withInstrument(instrument: Option[Instrument]) = copy[Once, ActorSysCount](instrument = instrument)

  def withActorSystem(actorSystem: Option[ActorSystem]) = copy[InstrumentCount, Once](actorSystem = actorSystem)

  def withComposer(composer: Option[Composer]) = copy[InstrumentCount, ActorSysCount](composer = composer)

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

  //TODO: Consider using a cache (http://spray.io/documentation/1.2.3/spray-caching/)
  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = CollectionUtils.createHashMultimap
  private var currentMusicTime: Long = 0

  def play(time: Long): Unit = {
    val musicalElements: Traversable[MusicalElement] = musicInfoMessageCache.get(time)
      .map(messages => messages.map(_.musicalElement)).getOrElse(Set())

    val phrase = Phrase().withMusicalElements(musicalElements)

    musicInfoMessageCache.remove(time)

    val responsePhrase = musicComposer.compose(phrase)
    responsePhrase.foreach(play)
  }

  override def play(musicalElement: MusicalElement): Unit = {
    if (!messageOnly) instrument.play(musicalElement)

    ActorUtils.broadcast(
      MusicInfoMessage(
        musicalElement,
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
