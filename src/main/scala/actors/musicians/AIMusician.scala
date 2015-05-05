package actors.musicians

import actors.composers.{Composer, RandomComposer}
import akka.actor.{ActorLogging, ActorSystem, Props}
import instruments.Instrument
import messages.{MusicInfoMessage, SyncMessage}
import representation.{MusicalElement, Phrase}
import utils.ActorUtils
import utils.builders.{Count, IsOnce, Once, Zero}

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
    B <: ActorSysCount: IsOnce]: AIMusician = {
    new AIMusician(this.asInstanceOf[AIMusicianBuilder[Once, Once]])
  }

  def buildProps[
    A <: InstrumentCount : IsOnce,
    B <: ActorSysCount: IsOnce]: Props = Props(build)
}

object AIMusician {
  def builder: AIMusicianBuilder[Zero, Zero] = new AIMusicianBuilder[Zero, Zero]
}

class AIMusician(builder: AIMusicianBuilder[Once, Once]) extends Musician with ActorLogging {
  private val instrument: Instrument = builder.instrument.get
  implicit private val actorSystem: ActorSystem = builder.actorSystem.get
  private val musicComposer: Composer = builder.composer.getOrElse(new RandomComposer)
  private val messageOnly: Boolean = builder.messageOnly.get

  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] = //TODO: Consider using a cache (http://spray.io/documentation/1.2.3/spray-caching/)
    new mutable.HashMap[Long, mutable.Set[MusicInfoMessage]]() with mutable.MultiMap[Long, MusicInfoMessage]
  private var currentMusicTime: Long = 0

  def play(time: Long): Unit = {
    val phraseBuilder = Phrase.builder
    val addMusicalElement = (m: MusicInfoMessage) => phraseBuilder.addMusicalElement(m.musicalElement)

    musicInfoMessageCache.get(time)
      .foreach(messages => messages.foreach(addMusicalElement))

    musicInfoMessageCache.remove(time)

    val responsePhrase = musicComposer.compose(phraseBuilder.build())
    if (! responsePhrase.isEmpty) {
      play(responsePhrase)
    }
  }

  override def play(musicalElement: MusicalElement): Unit = {
    if (! messageOnly) instrument.play(musicalElement)

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
