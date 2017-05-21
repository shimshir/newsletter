package de.springer.newsletter.services

import akka.actor.ActorRef
import akka.pattern.ask
import de.springer.newsletter.actors.StorageActor.Messages._
import de.springer.newsletter.models._

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.reflect.ClassTag

object DataServices {

  trait DataService[T, ID] {
    def create(item: T): Future[ID]
    def list: Future[Seq[T]]
    def storageActorRef: ActorRef
  }

  class GenericDataService[T : ClassTag, ID : ClassTag](val storageActorRef: ActorRef) extends DataService[T, ID] {
    def create(item: T): Future[ID] = (storageActorRef ? Store(item))(1.second).mapTo[ID]
    def list: Future[Seq[T]] = (storageActorRef ? List)(1.second).mapTo[Seq[T]]
  }

  class CategoryService(storageActorRef: ActorRef) extends GenericDataService[Category, CategoryId](storageActorRef)
  class BookService(storageActorRef: ActorRef) extends GenericDataService[Book, BookId](storageActorRef)
  class SubscriberService(storageActorRef: ActorRef) extends GenericDataService[Subscriber, SubscriberId](storageActorRef)
}


