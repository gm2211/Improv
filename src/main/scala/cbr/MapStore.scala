package cbr

trait MapStore[K, V] {
  def get(key: K): V
  def put(key: K, value: V): Unit
  def remove(key: K): Unit
  def removeAll(): Unit
}
