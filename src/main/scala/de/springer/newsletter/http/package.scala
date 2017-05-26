package de.springer.newsletter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, DataService, SubscriberService}
import de.springer.newsletter.services.NewsletterService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import sext._

package object http extends LazyLogging {
  import de.springer.newsletter.models.JsonProtocols._

  private def createPostPath[T](dataService: DataService[T], pathValue: String)
                               (implicit um: FromRequestUnmarshaller[T]) =
    path(pathValue) {
      post {
        entity(as[T]) { item =>
          logger.debug(s"Received POST /$pathValue with body:\n${item.getClass.getSimpleName}\n${item.valueTreeString}")
          onSuccess(dataService.create(item)) { stored =>
            logger.debug(s"Returning status ${Created.toString}")
            complete(Created)
          }
        }
      }
    }

  def categoryRoute(categoryService: CategoryService): Route = createPostPath(categoryService, "categories")

  def bookRoute(bookService: BookService): Route = createPostPath(bookService, "books")

  def subscriberRoute(subscriberService: SubscriberService): Route = createPostPath(subscriberService, "subscribers")

  def newsletterRoute(newsletterService: NewsletterService): Route =
    path("newsletters") {
      get {
        logger.debug(s"Received GET /newsletters")
        onSuccess(newsletterService.newsletters) { newsletters =>
          logger.debug(s"Returning ${newsletters.size} newsletters")
          complete(OK, newsletters)
        }
      }
    }

  def categorizedBooksRoute(newsletterService: NewsletterService): Route =
    path("categorized-books") {
      get {
        logger.debug(s"Received GET /categorized-books")
        onSuccess(newsletterService.categorizedBooks) { categorizedBooksTree =>
          logger.debug(s"Returning categorizedBooks")
          complete(OK, categorizedBooksTree)
        }
      }
    }

  private def serverRoutes(categoryService: CategoryService,
                           bookService: BookService,
                           subscriberService: SubscriberService,
                           newsletterService: NewsletterService)
                          (implicit actorSystem: ActorSystem, mat: Materializer) =
    categoryRoute(categoryService) ~
      bookRoute(bookService) ~
      subscriberRoute(subscriberService) ~
      newsletterRoute(newsletterService) ~
      categorizedBooksRoute(newsletterService)

  def server(categoryService: CategoryService,
             bookService: BookService,
             subscriberService: SubscriberService,
             newsletterService: NewsletterService)
            (port: Int)
            (implicit actorSystem: ActorSystem, mat: Materializer): Future[Http.ServerBinding] = {
    logger.debug(s"Starting server on port $port")
    Http().bindAndHandle(serverRoutes(categoryService, bookService, subscriberService, newsletterService), "0.0.0.0", port) map { binding =>
      val actualPort = binding.localAddress.getPort
      logger.info(s"Server started on port $actualPort")
      binding
    }
  }


}
