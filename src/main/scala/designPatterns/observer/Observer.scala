package designPatterns.observer

trait Observer {
  def notify(eventNotification: EventNotification): Unit
}
