package storage

import java.util

import cbr.CaseSolutionStore
import com.hazelcast.config.{Config, MapStoreConfig, XmlConfigBuilder}
import com.hazelcast.core.{Hazelcast, MapStore}

import scala.collection.JavaConversions._

class HazelcastStore[CaseSolution : Manifest](filename: String) extends CaseSolutionStore[CaseSolution] {
  private val mapdbStore = new MapDBMapStoreWrapper[String, CaseSolution](filename)

  /**
   * Loads the underlying map
   * @return The loaded map
   */
  override protected def loadMap: cbr.MapStore[String, CaseSolution] = {
    val config = HazelcastUtils.genConfigForMapStore(filename, mapdbStore)
    val hazelcast = Hazelcast.newHazelcastInstance(config)
    new cbr.MapStore[String, CaseSolution] {
      private val hazelcastMap = hazelcast.getMap[String, CaseSolution](filename)

      override def get(key: String): Option[CaseSolution] = Option(hazelcastMap.get(key))

      override def put(key: String, value: CaseSolution): Unit = hazelcastMap.put(key, value)

      override def remove(key: String): Unit = hazelcastMap.remove(key)

      override def removeAll(): Unit = hazelcastMap.foreach(hazelcastMap.remove)

      override def commit(): Unit = ()
    }
  }
}

object HazelcastUtils {
  def genConfigForMapStore[CaseSolution](dbName: String, store: MapStore[String, CaseSolution]): Config = {
    val mapStoreConfig = new MapStoreConfig()
      .setEnabled(true)
      .setImplementation(store)

    val config = new XmlConfigBuilder().build().setProperty("hazelcast.logging.type", "none")
    config.getMapConfig(dbName)
      .setMapStoreConfig(mapStoreConfig)

    config
  }
}

class MapDBMapStoreWrapper[K, V : Manifest](filename: String) extends MapStore[K, V] {
  private val mapdb = MapDBMapStore.loadFromFile[K, V](filename)

  override def delete(key: K): Unit = {
  }

  override def deleteAll(keys: util.Collection[K]): Unit = keys.foreach(delete)

  override def store(key: K, value: V): Unit = mapdb.put(key, value)

  override def storeAll(map: util.Map[K, V]): Unit = map.foreach((store _).tupled)

  override def loadAll(keys: util.Collection[K]): util.Map[K, V] = keys.map( key => (key, load(key))).toMap[K, V]

  override def loadAllKeys(): java.lang.Iterable[K] = mapdb.keySet()

  override def load(key: K): V = mapdb.get(key).getOrElse(manifest[V].getClass.newInstance().asInstanceOf)
}
