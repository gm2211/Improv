package representation

import utils.collections.CollectionUtils

object MusicGenre {
  val availableGenres = List(Jazz, Rock, Blues, Pop)
  def randomGenre = CollectionUtils.chooseRandom(availableGenres).get
}

sealed trait MusicGenre

case object Jazz extends MusicGenre
case object Rock extends MusicGenre
case object Blues extends MusicGenre
case object Pop extends MusicGenre
