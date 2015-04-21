package actors.musicians

import actors.composers.{Composer, RandomComposer}
import akka.actor.{ActorLogging, ActorSystem, Props}
import instruments.{Instrument, OvertoneInstrument}
import messages.{MusicInfoMessage, SyncMessage}
import representation.{MusicalElement, Phrase}
import utils.ActorUtils

import scala.collection.mutable

case class AIMusicianBuilder(
                              var instrument: Option[Instrument] = None,
                              var actorSystem: Option[ActorSystem] = None,
                              var composer: Option[Composer] = None) {
  def withInstrument(instrument: Option[Instrument]) = copy(instrument = instrument)
  def withActorSystem(actorSystem: Option[ActorSystem]) = copy(actorSystem = actorSystem)
  def withComposer(composer: Option[Composer]) = copy(composer = composer)

  def build: AIMusician = {
    require(actorSystem.isDefined)
    new AIMusician(this)
  }
  def buildProps: Props = Props(build)
}

object AIMusician {
  def builder: AIMusicianBuilder = new AIMusicianBuilder

  def props(instrument: Option[Instrument],
            actorSystem: Option[ActorSystem],
            composer: Option[Composer]): Props = {
    val musician = AIMusician.builder
      .withInstrument(instrument)
      .withActorSystem(actorSystem)
      .withComposer(composer)

    Props(musician.build)
  }
}

class AIMusician(builder: AIMusicianBuilder) extends Musician with ActorLogging {
  private val instrument: Instrument = builder.instrument.getOrElse(new OvertoneInstrument)
  private val actorSystem: ActorSystem = builder.actorSystem.get
  private val musicComposer: Composer = builder.composer.getOrElse(new RandomComposer)

  private val musicInfoMessageCache: mutable.MultiMap[Long, MusicInfoMessage] =
    new mutable.HashMap[Long, mutable.Set[MusicInfoMessage]]() with mutable.MultiMap[Long, MusicInfoMessage]
  private var currentMusicTime: Long = 0

  def play(time: Long): Unit = {
    val phraseBuilder = Phrase.builder
    val addMusicalElement = (m: MusicInfoMessage) => phraseBuilder.addMusicalElement(m.musicalElement)

    musicInfoMessageCache.get(time)
      .foreach(_.foreach(addMusicalElement))

    musicInfoMessageCache.remove(time)

    play(musicComposer.compose(phraseBuilder.build()))
  }


  override def play(musicalElement: MusicalElement): Unit = {
    instrument.play(musicalElement)
    ActorUtils.broadcast(actorSystem, MusicInfoMessage(musicalElement, currentMusicTime))
  }

  override def receive = {
    case m: SyncMessage =>
      log.debug(s"Received Sync at time: ${m.time}")
      log.debug(s"instrument: $instrument")

      currentMusicTime = m.time
      play(m.time)

    case m: MusicInfoMessage =>
      log.debug(s"At time ${m.time}, received music info: $m")
      musicInfoMessageCache.addBinding(m.time, m)
  }
}
