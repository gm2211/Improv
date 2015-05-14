package storage

import java.io.File

import cbr.MapStore
import com.google.common.io.Files
import org.mapdb.{DB, DBMaker, HTreeMap}

import scala.collection.JavaConversions._

object MapDBMapStore {
  def loadFromFile[K, V](filename: String) = {
    val db = DBMaker.fileDB(new File(filename)).make()
    new MapDBMapStore[K, V](db, Files.getNameWithoutExtension(filename))
  }
}

class MapDBMapStore[K, V](private val db: DB, mapName: String) extends MapStore[K, V] {
  private val db_map: HTreeMap[K, V] = db.hashMap(mapName)

  override def get(key: K): V = db_map.get(key)

  override def remove(key: K): Unit = {
    db_map.remove(key)
    db.commit()
  }

  override def put(key: K, value: V): Unit = {
    db_map.put(key, value)
    db.commit()
  }

  def keySet(): Set[K] = db_map.keySet().toSet

  def close(): Unit = db.close()
}
