package com.leeavital.passman

import javafx.application.Application
import javafx.stage.Stage

import com.leeavital.passman.scenes.{MainScene, LoginScene}

sealed trait PassmanEvent
case object UnlockFailed extends PassmanEvent

trait MainView {
  def passwordWasCopied(str: String)

  def onUnlockFailed: Unit

  def onUnlockSucceeded: Unit

  def showUnlockedZeroState(avaibleLogins: AvailableLogins): Unit
}

class TopView extends Application with MainView {

  val controller = new Controller(this)

  var unlockStage = new LoginScene(controller.tryUnlock)
  val mainScene = new MainScene(
    controller.selectAvailableLogin,
    addLoginMessage => controller.addLogin(addLoginMessage),
    controller.deleteLogin)

  var stage: Stage = null

  @throws(classOf[Exception])
  def start(primaryStage: Stage) {

    stage = primaryStage

    primaryStage.setScene(unlockStage.scene)
    primaryStage.show
  }

  override def onUnlockFailed: Unit = {
    unlockStage.unlockFailed()
  }

  override def showUnlockedZeroState(availableLogins: AvailableLogins): Unit = {
    mainScene.setAvailableLogins(availableLogins)

    stage.setScene(mainScene.scene)
  }

  override def onUnlockSucceeded: Unit = {
    unlockStage.unlockSucceeded
  }

  override def passwordWasCopied(label: String) = {
    mainScene.setPasswordCopied(label)
  }
}

