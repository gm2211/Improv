package tests.utils.builders


sealed trait Count
case class Zero() extends Count
case class Once() extends Count

