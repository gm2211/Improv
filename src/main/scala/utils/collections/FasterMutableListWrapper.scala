package utils.collections

import utils.ReflectionUtils

import scala.collection.mutable

class FasterMutableListWrapper[A](private val mutableList: mutable.MutableList[A]) extends mutable.MutableList[A] {
  def updateLast(elem: A): Unit = {
    val last0: Option[mutable.LinkedList[A]] = ReflectionUtils.getField("last0", mutableList)
    last0.foreach(lastElem => lastElem.update(0, elem))
  }
}
