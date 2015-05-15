package storage

import cbr.{CaseSolutionStore, MapStore}

class MapDBSolutionStore[CaseSolution](private val filename: String) extends CaseSolutionStore[CaseSolution] {
  /**
   * Loads the underlying map
   * @return The loaded map
   */
  override protected def loadMap: MapStore[String, CaseSolution] = MapDBMapStore.loadFromFile(filename)
}
