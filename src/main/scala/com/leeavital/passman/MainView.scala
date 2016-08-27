package com.leeavital.passman

/**
  * Created by lee on 8/27/16.
  */
class Controller(view: MainView) {

  def tryUnlock(str: String): Unit = {

    if (str == "password") {
      view.onUnlockSucceeded
    } else {

      view.onUnlockFailed

    }
  }
}
