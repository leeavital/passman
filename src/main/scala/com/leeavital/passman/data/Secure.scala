package com.leeavital.passman.data

/**
  * Created by lee on 8/28/16.
  */
sealed trait Secure[T] {
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

case class BadPassword[T] extends Secure[T] {
  override def map[R](fn: (T) => R): Secure[R] = {
    BadPassword[R]
  }

  override def flatMap[R](fn: (T) => Secure[R]): Secure[R] = {
    BadPassword[R]
  }
}
