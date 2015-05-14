package utils

import com.twitter.chill.KryoInjection

object SerialisationUtils {

  def serialise[A](obj: A): Array[Byte] = KryoInjection(obj)

  def serialise[A](obj: A, filename: String): Boolean =
    IOUtils.write(filename, serialise(obj))

  def deserialise[A](bytes: Array[Byte]): Option[A] =
    KryoInjection.invert(bytes).map(_.asInstanceOf[A]).toOption

  def deserialise[A](filename: String): Option[A] =
    IOUtils.read(filename).flatMap{ case bytes => deserialise(bytes) }
}
