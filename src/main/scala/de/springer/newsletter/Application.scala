package de.springer.newsletter

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.{Book, Category, Subscriber}
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, SubscriberService}
import de.springer.newsletter.services.NewsletterService

import scala.concurrent.Future

object Application extends App with LazyLogging {
  def start(port: Int): Future[ServerBinding] = {
    implicit val actorSystem = ActorSystem("newsletter-actor-system")
    implicit val mat = ActorMaterializer()
    implicit val ec = actorSystem.dispatcher

    val categoryStorageActorRef = actorSystem.actorOf(StorageActor.props[Category], "category-storage-actor")
    val categoryService = new CategoryService(categoryStorageActorRef)

    val bookStorageActorRef = actorSystem.actorOf(StorageActor.props[Book], "book-storage-actor")
    val bookService = new BookService(bookStorageActorRef)

    val subscriberStorageActorRef = actorSystem.actorOf(StorageActor.props[Subscriber], "subscriber-storage-actor")
    val subscriberService = new SubscriberService(subscriberStorageActorRef)

    val newsletterService = new NewsletterService(categoryService, bookService, subscriberService)

    import de.springer.newsletter.stubs.StubData._
    categories.foreach(categoryService.create)
    books.foreach(bookService.create)
    subscribers.foreach(subscriberService.create)

    http.server(categoryService, bookService, subscriberService, newsletterService)(port)
  }

  logger.debug("Starting application")
  start(port = 8080)
}
