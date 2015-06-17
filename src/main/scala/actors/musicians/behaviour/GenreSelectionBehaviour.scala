package actors.musicians.behaviour

import messages.Message
import messages.consensus._
import representation.MusicGenre

class GenreSelectionBehaviour extends AIMusicianBehaviour with ReceiveBehaviour {
  var votingInProgress = false
  var votedAlready = false

  override def apply(message: Message): Unit = message match {
    case VoteRequest(director, GenreElection) if !votedAlready =>
      musician.foreach{ mus =>
        val genrePreference = GenrePreference(mus.musicGenre.getOrElse(MusicGenre.randomGenre))
        director ! VoteResponse(mus.self, GenreElection, genrePreference)
        votedAlready = true
      }

    case FinalDecision(_, preference@GenrePreference(_))=>
      votedAlready = false
      votingInProgress = false
      println(s"${preference.genre} has been chosen")
      musician.foreach(_.musicGenre = Some(preference.genre))

    case c: ConsensusMessage =>
      () // Ignore other types of consensus message

    case _ =>
      musician.foreach { mus =>
        if (mus.musicGenre.isEmpty && !votingInProgress) {
          val voteRequest = VoteRequest(mus.self, GenreElection)
          mus.directorIdentity.foreach(_ ! voteRequest)
          votingInProgress = true
        }
      }
  }
}
