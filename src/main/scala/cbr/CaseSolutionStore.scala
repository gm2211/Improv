package cbr

import java.util.UUID

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional

trait CaseSolutionStore[CaseSolution] {
  @Optional("Do not serialise the map")
  private var map: Option[MapStore[String, CaseSolution]] = None

  private def getMap = {
    if (map.isEmpty) {
      map = Some(loadMap)
    }
    map.get
  }

  /**
   * Loads the underlying map
   * @return The loaded map
   */
  protected def loadMap: MapStore[String, CaseSolution]

  /**
   * Adds a solution to the store
   * @param caseSolution Solution to be added
   * @return UUID String of the solution
   */
  def addSolution(caseSolution: CaseSolution): String = {
    val newKey = UUID.randomUUID().toString
    getMap.put(newKey, caseSolution)
    newKey
  }

  /**
   * Retrieve a solution by ID
   * @param solutionID ID of the solution to be retrived
   * @return An Option of a solution
   */
  def getSolution(solutionID: String): Option[CaseSolution] = Option(getMap.get(solutionID))

  /**
   * Removes a solution from the store
   * @param solutionID ID of the solution to be removed
   */
  def removeSolution(solutionID: String): Unit = getMap.remove(solutionID)
}
