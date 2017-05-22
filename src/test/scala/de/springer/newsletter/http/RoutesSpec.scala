package de.springer.newsletter.http

import akka.http.scaladsl.model._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import de.springer.newsletter.actors.StorageActor.Messages.Stored
import de.springer.newsletter.models.{Book, Category, Newsletter, Subscriber}
import org.scalatest._
import spray.json._
import org.mockito.Mockito._
import org.mockito.Matchers._
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, SubscriberService}
import org.scalatest.mockito.MockitoSugar
import de.springer.newsletter.models.JsonProtocols._
import de.springer.newsletter.services.NewsletterService

import scala.concurrent.Future

class RoutesSpec extends FlatSpec with Matchers with MockitoSugar with ScalatestRouteTest {
  val (mockCategoryService, mockBookService, mockSubscriberService, mockNewsletterService) =
    (mock[CategoryService], mock[BookService], mock[SubscriberService], mock[NewsletterService])

  "POST /categories" should "create a category" in {
    when(mockCategoryService.create(any())).thenReturn(Future.successful(Stored))

    postRequest("/categories", Category("science", "Science", None)) ~>
      categoryRoute(mockCategoryService) ~> check {
      status shouldEqual StatusCodes.Created
    }
  }

  "POST /books" should "create a book" in {
    when(mockBookService.create(any())).thenReturn(Future.successful(Stored))

    postRequest("/books", Book("bookId", "Britney Spears' Heart to Heart", Set.empty)) ~>
      bookRoute(mockBookService) ~> check {
      status shouldEqual StatusCodes.Created
    }
  }

  "POST /subscribers" should "create a subscriber" in {
    when(mockSubscriberService.create(any())).thenReturn(Future.successful(Stored))

    postRequest("/subscribers", Subscriber("britney", "britney@spears.com", Set.empty)) ~>
      subscriberRoute(mockSubscriberService) ~> check {
      status shouldEqual StatusCodes.Created
    }
  }

  "GET /newsletters" should "return newsletters" in {
    val stubNewsletters = Set(Newsletter("email@email.em", Set.empty))
    when(mockNewsletterService.createNewsletters()).thenReturn(Future.successful(stubNewsletters))

    Get("/newsletters") ~> newsletterRoute(mockNewsletterService) ~> check {
      status shouldEqual StatusCodes.OK
      responseAs[Set[Newsletter]] shouldEqual stubNewsletters
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
