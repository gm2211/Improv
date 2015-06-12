package designPatterns.observer

import scala.collection.mutable

trait Observable {
  val observers = mutable.HashSet[Observer]()

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
