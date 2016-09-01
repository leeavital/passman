package com.leeavital.passman.scenes

import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout.{GridPane, StackPane}

import com.leeavital.passman.fx.FxUtils


class LoginScene(tryUnlock: Function[String, Unit]) {

  import FxUtils._

  lazy private val sceneTitle = text("Hello world") withFontSize (20)

  lazy private val userLabel: Label = label("enter your password")

  lazy private val passwordField = passwordInput
  userLabel.setLabelFor(userLabel)

  lazy private val failureMessage = label("")

  passwordField onSubmit (() => {
    tryUnlock.apply(passwordField.getText)
  })

  lazy private val signInButton = rightAlign((button("sign in") onClick (() => {
    tryUnlock.apply(passwordField.getText)
  })))

  lazy private val gridPane: GridPane = layoutRows(
    Seq(text("Welcome to Passmann") withFontSize(20)),
    Seq(userLabel, passwordField),
    Seq(signInButton),
    Seq(failureMessage)
  )

  lazy val stackPane = new StackPane(gridPane)

  val scene: Scene = new Scene(stackPane, 400, 400)

  def unlockFailed(): Unit = {
    failureMessage.setText("login failed")
  }

  def unlockSucceeded: Unit = {
    failureMessage.setText("login succeeded")
  }
}
