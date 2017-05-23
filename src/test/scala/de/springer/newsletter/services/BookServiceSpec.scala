package de.springer.newsletter.services

import akka.actor.{ActorRef, ActorSystem}
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.Book
import de.springer.newsletter.services.DataServices.BookService
import org.scalatest._


class BookServiceSpec extends FlatSpec with Matchers with DataServiceSpec {
  val actorSystem = ActorSystem("test-bookService-system")
  implicit val ec = actorSystem.dispatcher

  "bookService" should "be able to create and list books" in {
    val bookStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props[Book], "book-test-actor")
    val bookService = new BookService(bookStorageActorRef)
    val expectedBooks = (0 to 9).map(id => Book(id.toString, "title", Set.empty))
    writeAndRead(bookService, expectedBooks.toSet) should contain only(expectedBooks: _*)
  }
}
