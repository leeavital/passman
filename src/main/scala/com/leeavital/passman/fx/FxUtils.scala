package com.leeavital.passman.fx

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout.{GridPane, HBox, VBox}
import javafx.scene.text.{Font, Text}
import javafx.scene.{Node, input}

object FxUtils {

  // decorators
  trait Fontable[T] {
    def withFontSize(i: Double): T
  }

  trait Actionable[T] {
    def onClick(callback: () => Any): T
  }

  def text(label: String): Text = {
    new Text(label)
  }

  def label(label: String): Label = {
    new Label(label)
  }

  def textInput: TextField = {
    new TextField
  }

  def passwordInput: PasswordField = {
    new PasswordField
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

  implicit class TextFieldOps(field: TextInputControl) {
    def onSubmit(fn: () => Unit) = {
      field.setOnKeyPressed(new EventHandler[input.KeyEvent] {
        override def handle(event: input.KeyEvent): Unit = {
          if (event.getCode == KeyCode.ENTER) {
            fn()
          }
        }
      })
      field
    }

    def onKeyrelease(fn: String => Unit) = {
      field.setOnKeyPressed(new EventHandler[KeyEvent] {
        override def handle(event: KeyEvent): Unit = {
          fn(field.getText)
        }
      })
      field
    }

    def withPrompt(label: String): TextInputControl = {
      field.setPromptText(label)
      field
    }
  }

  implicit class GridDecorations(gridPane: GridPane) {
    def withPadding(hGap: Double = 20, vGap: Double = 20) = {
      gridPane.setHgap(hGap)
      gridPane.setVgap(vGap)
      gridPane
    }

    def centerAligned = {
      gridPane.setAlignment(Pos.CENTER)
      gridPane
    }

    def topLeftAligned = {
      gridPane.setAlignment(Pos.TOP_LEFT)
      gridPane
    }
  }

  implicit class LabelDecorator(label: Label) {
    def forControl(node: Node): Label = {
      label.setLabelFor(node)
      label
    }
  }

  // layout stuff
  def layoutRows[T <: Node](rows: Seq[T]*): GridPane = {
    val pane = new GridPane
    val numberOrColumns = rows.map(_.length).max

    rows.zipWithIndex.foreach {
      case (shapes, rowNum) =>
        val localNumberOfColumns = shapes.length

        shapes.zipWithIndex.foreach {
          case (node, column) => {
            if (localNumberOfColumns - 1 == column) {
              pane.add(node, column, rowNum, numberOrColumns - column, 1)
            } else {
              println(node)
              pane.add(node, column, rowNum)
            }
          }
        }
    }

    pane.setAlignment(Pos.CENTER)
    pane.setHgap(20)
    pane.setVgap(20)

    pane
  }

  def layoutVertically[T <: Node](rows: T*): Node = {
    val box = new VBox
    rows.foreach(box.getChildren.add)
    box
  }

  def layoutHorizontally[T <: Node](cols: T*): Node = {
    val box = new HBox
    cols.foreach(box.getChildren.add)
    box
  }

  def layoutTabs[T <: Node](tabs: (String, T)*): TabPane = {
    val pane = new TabPane
    tabs.map {
      case (name, content) =>
        val tab = new Tab
        tab.setClosable(false)
        tab.setContent(content)
        tab.setText(name)
        tab
    }.foreach(pane.getTabs.add _)

    pane
  }

}
