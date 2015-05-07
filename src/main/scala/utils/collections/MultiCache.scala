package utils.collections


import java.util.UUID
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import com.google.common.cache.{Cache, CacheBuilder, RemovalListener, RemovalNotification}
import scala.collection.mutable
import utils.ImplicitConversions.anyToRunnable

object MultiCache {
  def buildDefault[K, V <: java.lang.Object](timeoutMS: Long, maxSize: Long): MultiCache[K, V] = {
    val builder = CacheBuilder.newBuilder()
      .expireAfterAccess(timeoutMS, TimeUnit.MILLISECONDS)
      .maximumSize(maxSize)
    new MultiCache[K, V](builder, timeoutMS)
  }
}

class MultiCache[K, V <: java.lang.Object](
    cacheBuilder: CacheBuilder[java.lang.Object, java.lang.Object],
    val valueExpiration: Long) extends mutable.MultiMap[K, V] with RemovalListener[UUID, V] {
  private val cache: Cache[UUID, V] = cacheBuilder.removalListener(this).build()
  private val multiMap: mutable.MultiMap[K, UUID] = CollectionUtils.createHashMultimap
  private val reverseMap: mutable.Map[UUID, K] = mutable.HashMap()
  private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

  executor.scheduleAtFixedRate(
    () => cache.cleanUp(),
    0,
    math.max(1000, valueExpiration), // Don't run cleanup too often to guarantee availability
    TimeUnit.MILLISECONDS)

  override def +=(kv: (K, mutable.Set[V])): MultiCache.this.type = {
    val (k, vs) = kv
    for (v <- vs) {
      val uuid = UUID.randomUUID()
      multiMap.addBinding(k, uuid)
      reverseMap.put(uuid, k)
      cache.put(uuid, v)
    }
    this
  }

  override def -=(key: K): MultiCache.this.type = {
    val uuids = multiMap.get(key)
    if (uuids.isDefined) {
      for (uuid <- uuids.get) {
        cache.invalidate(uuid)
        reverseMap.remove(uuid)
      }
      multiMap.remove(key)
    }
    this
  }

  override def get(key: K): Option[mutable.Set[V]] = {
    multiMap.get(key)
      .map{ uuids =>
        uuids.flatMap{ uuid =>
          Option(cache.getIfPresent(uuid))
        }
      }
  }

  override def iterator: Iterator[(K, mutable.Set[V])] = new Iterator[(K, mutable.Set[V])] {
    private val mapIterator = multiMap.iterator

    override def hasNext: Boolean = mapIterator.hasNext

    override def next(): (K, mutable.Set[V]) = {
      val (key, uuids) = mapIterator.next()
      (key, uuids.map(cache.getIfPresent))
    }
  }

  override def onRemoval(notification: RemovalNotification[UUID, V]): Unit = {
    val uuid = notification.getKey
    reverseMap.get(uuid).foreach(key => multiMap.removeBinding(key, uuid))
    reverseMap.remove(uuid)
  }

  override def entryExists(key: K, p: (V) => Boolean): Boolean = {
    val pp = (uuid: UUID) => Option(cache.getIfPresent()).exists(p)
    multiMap.entryExists(key, pp)
  }

  override def addBinding(key: K, value: V): MultiCache.this.type = this.+=((key, mutable.Set(value)))

  override def contains(key: K): Boolean = multiMap.contains(key)

  override def size: Int = multiMap.size
}
