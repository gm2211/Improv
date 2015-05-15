package cbr

trait MapStore[K, V] {
  /**
   * Gets an option the value corresponding to the key in the map. None if absent
   * @param key Key of the entry to be looked up
   * @return Some(V) or None
   */
  def get(key: K): Option[V]

  /**
   * Adds a new entry to the map. If the key already exists the value is overwritten
   * @param key Key of the entry to be added to the map
   * @param value Value of the entry to be added to the map
   */
  def put(key: K, value: V): Unit

  /**
   * Removes an entry with key `key` from the map
   * @param key Key of the entry to be removed
   */
  def remove(key: K): Unit

  /**
   * Removes all the entries in the map
   */
  def removeAll(): Unit

  /**
   * Commits the changes to the map
   */
  def commit(): Unit
}
