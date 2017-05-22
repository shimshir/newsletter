package de.springer.newsletter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.{Book, Category, Subscriber}
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, SubscriberService}
import de.springer.newsletter.services.NewsletterService

object Application extends App {
  implicit val actorSystem = ActorSystem("newsletter-actor-system")
  implicit val mat = ActorMaterializer()
  val categoryStorageActorRef = actorSystem.actorOf(StorageActor.props[Category], "category-storage-actor")
  val categoryService = new CategoryService(categoryStorageActorRef)

  val bookStorageActorRef = actorSystem.actorOf(StorageActor.props[Book], "book-storage-actor")
  val bookService = new BookService(bookStorageActorRef)

  val subscriberStorageActorRef = actorSystem.actorOf(StorageActor.props[Subscriber], "subscriber-storage-actor")
  val subscriberService = new SubscriberService(subscriberStorageActorRef)

  val newsletterService = new NewsletterService(categoryService, bookService, subscriberService)

  val server = http.server(categoryService, bookService, subscriberService, newsletterService)
}
