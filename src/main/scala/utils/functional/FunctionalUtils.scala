package utils.functional

import scala.util.Try

object FunctionalUtils {
  def combineOr[A, B](functions: Iterable[PartialFunction[A, B]]): PartialFunction[A, B] = {
    functions.foldLeft(PartialFunction.empty[A, B])((acc, fn) => acc orElse fn)
  }
  def combineAnd[A](functions: Iterable[PartialFunction[A, A]]): PartialFunction[A, A] = {
    functions.foldLeft(PartialFunction.empty[A, A])((acc, fn) => acc andThen fn)
  }

  def memoized[A, B](function: A => B) = new MemoizedFunc[A, B](function)
  def memoized[B](value: => B) = new MemoizedValue[B](() => value)

  def flip[A, B, C](fn: (A, B) => C): (B, A) => C = (b: B, a: A) => fn(a, b)

  def flip[A, B, C](fn: A => B => C): B => A => C = (b: B) => (a: A) => fn(a)(b)

  def tryFinally[A](task: => A,finallyFunction: () => Unit): Try[A] = {
    val result = Try(task)
    finallyFunction()
    result
  }
}
