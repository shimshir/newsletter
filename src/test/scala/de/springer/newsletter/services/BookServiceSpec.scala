package de.springer.newsletter.services

import akka.actor.{ActorRef, ActorSystem}
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.{Book, BookId}
import de.springer.newsletter.services.DataServices.BookService
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class BookServiceSpec extends FlatSpec with Matchers {
  val actorSystem = ActorSystem("test-bookService-system")
  implicit val ec = actorSystem.dispatcher

  "create" should "create a book" in {
    val bookStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props(BookId), "create-book-test-actor")
    val bookService = new BookService(bookStorageActorRef)
    val baseBook = Book(None, "title", Nil)
    val bookIdsFut = Future.sequence((0 to 9).map(_ => bookService.create(baseBook)))
    val bookIds: Seq[BookId] = Await.result(bookIdsFut, 1.second)
    bookIds should contain only((0 to 9).map(_.toString).map(BookId): _*)
  }

  "list" should "return a list of all books" in {
    val bookStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props(BookId), "list-book-test-actor")
    val bookService = new BookService(bookStorageActorRef)
    val baseBook = Book(None, "title", Nil)
    val _ = Future.sequence((0 to 9).map(_ => bookService.create(baseBook)))
    val booksFut = bookService.list
    val books: Seq[Book] = Await.result(booksFut, 1.second)
    books should contain only((0 to 9).map(_.toString).map(BookId).map(baseBook.withId): _*)
  }
}
