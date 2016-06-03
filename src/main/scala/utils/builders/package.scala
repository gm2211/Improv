package utils

package object builders {
  type IsAtLeastOnce[T] = =:=[T, AtLeastOnce]
  type IsZero[T] = =:=[T, Zero]
}
