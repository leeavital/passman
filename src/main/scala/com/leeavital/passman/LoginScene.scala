package com.leeavital.passman

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.input.MouseEvent
import javafx.scene.layout.{GridPane, HBox}
import javafx.scene.text.{Font, Text}
import javafx.scene.{Node, Scene}


object FxUtils {

  // decoratros
  trait Fontable[T] {
    def withFontSize(i: Double): T
  }

  trait Actionable[T] {
    def onClick(callback: () => Any): T
  }

  def text(label: String): Text = {
    new Text( label )
  }

  def label(label: String): Label = {
    new Label(label)
  }

  def textInput: TextField = {
    new TextField
  }

  def button(label: String): Button = {
    new Button(label)
  }

  def rightAlign(node: Node): HBox = {
    val box = new HBox(node)
    box.setAlignment(Pos.BASELINE_RIGHT)
    box
  }

  // implementations of decorators
  implicit class FontableText(txt: Text) extends Fontable[Text] {
    def withFontSize(i: Double): Text = {
      txt.setFont(Font.font(i))
      txt
    }
  }

  implicit class ActionableButton(button: Button) extends Actionable[Button] {
    override def onClick(callback: () => Any): Button = {
      button.setOnMouseClicked(new EventHandler[MouseEvent] {
        override def handle(event: MouseEvent): Unit = {
          callback.apply()
        }
      })
      button
    }
  }


  // layout stuff
  def layoutRows[T <: Node](rows: Seq[T]*) = {
    val pane = new GridPane
    val numberOrColumns = rows.map(_.length).max

    rows.zipWithIndex.foreach {
      case (shapes, rowNum) =>
        val localNumberOfColumns = shapes.length

        shapes.zipWithIndex.foreach {
          case (node, column) => {
            if (localNumberOfColumns - 1 == column) {
              println("special", node)
                pane.add(node, column, rowNum, numberOrColumns - column, 1)
            } else {
              println(node)
              pane.add(node, column, rowNum)
            }
          }
        }
    }
    pane
  }
}


class LoginScene(
  tryUnlock: Function[String, Unit]) {


  import FxUtils._

  private val sceneTitle = text("Hello world") withFontSize(20)

  private val userLabel: Label = label("password?")

  private val userField = textInput
  userLabel.setLabelFor(userLabel)

  private val signInButton = rightAlign ((button("sign in") onClick (() => {
    tryUnlock.apply(userField.getText)
  })))

  private val gridPane: GridPane = layoutRows(
    Seq(sceneTitle),
    Seq(userLabel, userField),
    Seq(signInButton),
    Seq(failureMessage)
  )

  lazy private val failureMessage = label("")

  gridPane.setAlignment(Pos.CENTER)
  gridPane.setHgap(20)
  gridPane.setVgap(20)

  val scene: Scene = new Scene(gridPane, 300, 300)

  def unlockFailed(): Unit = {
    failureMessage.setText("login failed")
  }

  def unlockSucceeded: Unit = {
    failureMessage.setText("login succeeded")
  }
}
