package designPatterns.observer

import scala.collection.mutable.ListBuffer

trait Observable {
  val observers = ListBuffer[Observer]()

  def addObserver(observer: Observer): Unit = {
    observers += observer
  }

  def removeObserver(observer: Observer): Unit = {
    observers -= observer
  }

  def notifyObservers(eventNotification: EventNotification): Unit = {
    observers.foreach(_.notify(eventNotification))
  }
}
