package com.leeavital.passman.data

/**
  * Secure is a monad for values that have been derived from a vault file.
  */
sealed trait Secure[+T] {
  def map[R](fn: T => R): Secure[R]

  def flatMap[R](fn: T => Secure[R]): Secure[R]

}

case class CorruptedFile[T](error: String) extends Secure[T] {
  override def map[R](fn: (T) => R): Secure[R] = {
    CorruptedFile[R](error)
  }

  override def flatMap[R](fn: (T) => Secure[R]): Secure[R] = {
    CorruptedFile[R](error)
  }
}


case class Ok[T](value: T) extends Secure[T] {
  override def map[R](fn: (T) => R): Secure[R] = {
    Ok(fn(value))
  }

  override def flatMap[R](fn: (T) => Secure[R]): Secure[R] = {
    fn(value)
  }
}

case object BadPassword extends Secure[Nothing] {
  override def map[R](fn: (Nothing) => R): Secure[R] = {
    BadPassword
  }

  override def flatMap[R](fn: (Nothing) => Secure[R]): Secure[R] = {
    BadPassword
  }
}
