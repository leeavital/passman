package com.leeavital.passman.scenes

import com.leeavital.passman.AddLoginMessage

/**
  * Created by lee on 8/30/16.
  */
class NewPasswordForm(addLogin: AddLoginMessage => Unit) {

  import com.leeavital.passman.fx.FxUtils._

  private lazy val nameInput = textInput withPrompt("name")
  private lazy val passField = passwordInput withPrompt("password")
  private lazy val passField2 = passwordInput withPrompt("password (repeat)")
  private lazy val status = label("")

  private lazy val grid =
    layoutRows(
      Seq(
        label("name") forControl nameInput,
        nameInput),

      Seq(
        label("password") forControl passField,
        passField),

      Seq(
        label("password (repeat)") forControl passField2,
        passField2
      ),

      Seq(
        rightAlign(button("add") onClick(() => {
          if (passField.getText != passField2.getText) {
            status.setText("passwords do not match")
          } else {
            addLogin(AddLoginMessage(nameInput.getText, passField.getText))
          }
        }))
      ),

      Seq(
        status
      )
    )

  def form = grid
}
