package minibrassgui

import scalafx.application
import scalafx.application.JFXApp
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Button
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle


object PrimaryStageMB {

  def run() = {

    val stage = new JFXApp {}
    stage.stage = new application.JFXApp.PrimaryStage {
      scene = new Scene {
        fill = DarkGray
        content = new HBox{
          spacing = 10
          padding = Insets(20)
          children = Seq(
            new Rectangle {
              width = 100
              height = 100
            },
            new Rectangle {
              width = 100
              height = 100
            },
            new Button {
              text = "ADD"
              onAction = { ae => fill = Red }
            }
          )
        }
      }
    }
  }

}
