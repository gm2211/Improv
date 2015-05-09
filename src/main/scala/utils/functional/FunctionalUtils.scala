package utils.functional

object FunctionalUtils {
  def memoized[A, B](function: A => B) = new MemoizedFunc[A, B](function)

  def flip[A, B, C](fn: (A, B) => C): (B, A) => C = (b: B, a: A) => fn(a, b)

  def flip[A, B, C](fn: A => B => C): B => A => C = (b: B) => (a: A) => fn(a)(b)
}
