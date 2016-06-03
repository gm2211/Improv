package utils.builders


sealed trait Count

case class Zero() extends Count

case class AtLeastOnce() extends Count

