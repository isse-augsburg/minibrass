import scalafx.application
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.beans.property.DoubleProperty
import scalafx.scene.Scene
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.paint.Color._
import scalafx.scene.shape.Rectangle

object HelloStageDemo extends JFXApp {
  stage = new application.JFXApp.PrimaryStage {
    title.value = "Hello Stage"
    width = 600
    height = 450
    scene = new Scene {
      fill = LightGreen
      content = new Rectangle {
        x = 25
        y = 40
        width = 100
        height = 100
        fill <== when (hover) choose Green otherwise Red
        onMouseEntered = { anything => println("ok seems to work")}
      }
    }
  }
  val speed = new DoubleProperty(this, "speed", 55)
  val subscription = speed.onChange { (_, oldValue, newValue) =>
    println(s"Value of property 'speed' is changing from $oldValue to $newValue\n")
  }

  speed() = 60
  subscription.cancel()
  speed.value = 25

  val base = DoubleProperty(15)
  val height = DoubleProperty(10)
  val area = DoubleProperty(0)

  area <== base * height / 2
  printValues()

  println("base to 20")
  base() = 20
  printValues()

  println("height to 5")
  height() = 5
  printValues()


  new Alert(AlertType.Information, "Hello Test").showAndWait()


  def printValues(): Unit = {
    println(f"base = ${base()}%4.1f, height = ${height()}%4.1f, area = ${area()}%5.1f\n")
  }
}












