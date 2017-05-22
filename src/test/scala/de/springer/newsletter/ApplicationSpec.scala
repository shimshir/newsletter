package de.springer.newsletter

import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.Await


class ApplicationSpec extends FlatSpec with Matchers {
  "Application" should "successfully start" in {
    val serverBindingFut = Application.start(0)
    val serverBinding = Await.result(serverBindingFut, 1.second)
    serverBindingFut.isCompleted shouldBe true
    serverBinding shouldNot be (null)
  }
}
