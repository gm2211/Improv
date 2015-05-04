package tests.utils

object FunctionalUtils {
  def flip[A, B, C](fn: (A, B) => C): (B, A) => C = (b: B, a: A) => fn(a, b)
  def flip[A, B, C](fn: A => B => C): B => A => C = (b: B) => (a: A) => fn(a)(b)
}
