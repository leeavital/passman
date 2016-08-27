package com.leeavital.passman

import javafx.application.Application
import javafx.stage.Stage


sealed trait PassmanEvent
case object UnlockFailed extends PassmanEvent

trait MainView {
  def onUnlockFailed: Unit

  def onUnlockSucceeded: Unit

  def showUnlockedZeroState: Unit
}

class TopView extends Application with MainView {

  val controller = new Controller(this)

  val unlockStage = new LoginScene(controller.tryUnlock)

  @throws(classOf[Exception])
  def start(primaryStage: Stage) {

    primaryStage.setScene(unlockStage.scene)
    primaryStage.show
  }

  override def onUnlockFailed: Unit = {
    unlockStage.unlockFailed()
  }

  override def showUnlockedZeroState: Unit = {
    throw new RuntimeException("not implemented")
  }

  override def onUnlockSucceeded: Unit = {
    unlockStage.unlockSucceeded
  }
}

