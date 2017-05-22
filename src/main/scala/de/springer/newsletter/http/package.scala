package de.springer.newsletter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.FromRequestUnmarshaller
import akka.stream.Materializer
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, DataService, SubscriberService}
import de.springer.newsletter.services.NewsletterService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import models.JsonProtocols._

package object http {
  private def createPostPath[T](dataService: DataService[T], pathValue: String)
                               (implicit um: FromRequestUnmarshaller[T]) =
    path(pathValue) {
      post {
        entity(as[T]) { item =>
          onSuccess(dataService.create(item)) { stored =>
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
        onSuccess(newsletterService.createNewsletters()) { newsletters =>
          complete(OK, newsletters)
        }
      }
    }

  private def serverRoutes(categoryService: CategoryService,
                           bookService: BookService,
                           subscriberService: SubscriberService,
                           newsletterService: NewsletterService)
                          (implicit actorSystem: ActorSystem, mat: Materializer) =
    categoryRoute(categoryService) ~ bookRoute(bookService) ~ subscriberRoute(subscriberService) ~ newsletterRoute(newsletterService)

  def server(categoryService: CategoryService, bookService: BookService, subscriberService: SubscriberService, newsletterService: NewsletterService)
            (implicit actorSystem: ActorSystem, mat: Materializer): Future[Http.ServerBinding] =
    Http().bindAndHandle(serverRoutes(categoryService, bookService, subscriberService, newsletterService), "0.0.0.0", 8080) map { binding =>
      println("Server started on port 8080")
      binding
    }

}
