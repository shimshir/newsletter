package de.springer.newsletter.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.springer.newsletter.actors.StorageActor.Messages.Stored
import de.springer.newsletter.models.Category
import org.scalatest._
import spray.json._
import org.mockito.Mockito._
import org.mockito.Matchers._
import de.springer.newsletter.services.DataServices.CategoryService
import org.scalatest.mockito.MockitoSugar
import de.springer.newsletter.models.JsonProtocols._

import scala.concurrent.Future

class RoutesSpec extends FlatSpec with Matchers with MockitoSugar with ScalatestRouteTest {
  "POST /categories" should "create a category" in {
    val mockService = mock[CategoryService]
    when(mockService.create(any())).thenReturn(Future.successful(Stored))

    postRequest("/categories", Category("science", "Science", None)) ~> categoryRoute(mockService) ~> check {
      status shouldEqual StatusCodes.Created
    }
  }

  def postRequest[T](path: String, body: T)
                    (implicit jsonFormat: JsonFormat[T]) =
    HttpRequest(
      HttpMethods.POST,
      uri = path,
      entity = HttpEntity(MediaTypes.`application/json`, body.toJson.compactPrint)
    )
}
