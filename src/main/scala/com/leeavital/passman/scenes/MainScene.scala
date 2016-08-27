package com.leeavital.passman.scenes

import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.{BorderPane, VBox}

import com.leeavital.passman.fx.FxUtils
import com.leeavital.passman.{AddLoginMessage, AvailableLogins}

/**
  * Created by lee on 8/27/16.
  */
class MainScene(
               selectAvailableLogin: String => Unit,
               addLogin: AddLoginMessage => Unit,
               deleteLogin: String => Unit) {

  var availableLogins: AvailableLogins = null

  import FxUtils._

  lazy private val newPasswordForm = new NewPasswordForm(addLogin)

  lazy private val logins = new VBox
  lazy private val loginsScroll = new ScrollPane()
  loginsScroll.setContent(logins)

  lazy private val content = layoutRows(
    Seq(loginsScroll))  topLeftAligned


  logins.setSpacing(20)

  private lazy val statusLabel = label("")

  lazy val stackPane = new BorderPane
  stackPane.setTop(
    textInput withPrompt("search...") onKeyrelease (searchText => {
      val search = searchText.toLowerCase
      val results = availableLogins.logins.filter(name => name.toLowerCase.contains(search))
      setAvailableLogins(results)
    }))
  stackPane.setCenter(content)
  stackPane.setBottom(statusLabel)

  content.prefWidthProperty.bind(content.widthProperty)
  loginsScroll.prefWidthProperty.bind(content.widthProperty)

  val tabPane = layoutTabs(
    ("passwords", stackPane),
    ("new password", newPasswordForm.form)
  )

  val scene = new Scene(tabPane, 400, 400)

  def setAvailableLogins(availableLogins: AvailableLogins): Unit = {
    this.availableLogins = availableLogins
    setAvailableLogins(availableLogins.logins)
  }

  def setPasswordCopied(label: String): Unit = {
    statusLabel.setText(s"Copied password for ${label} to clipboard")
  }

  private def setAvailableLogins(names: Seq[String]) = {
    tabPane.getSelectionModel.select(0)

    logins.getChildren.clear
    names.foreach(login => {
      val listItem =
        layoutHorizontally(
          button(login) onClick (() => {
            println(s"clicked on login ${login}")
            selectAvailableLogin(login)
          }),

          button("x") onClick (() => {
            deleteLogin(login)
          })
        )

      logins.getChildren.add(listItem)
    })
  }
}
