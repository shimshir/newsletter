package de.springer.newsletter.services

import akka.actor.ActorRef
import akka.pattern.ask
import de.springer.newsletter.actors.StorageActor.Messages._
import de.springer.newsletter.models._

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.reflect.ClassTag

object DataServices {

  trait DataService[T] {
    def create(item: T): Future[Stored]
    def list: Future[Set[T]]
    def storageActorRef: ActorRef
  }

  class GenericDataService[T: ClassTag](val storageActorRef: ActorRef) extends DataService[T] {
    def create(item: T): Future[Stored] = (storageActorRef ? Store(item))(1.second).mapTo[Stored]
    def list: Future[Set[T]] = (storageActorRef ? List)(1.second).mapTo[Set[T]]
  }

  class CategoryService(storageActorRef: ActorRef) extends GenericDataService[Category](storageActorRef)
  class BookService(storageActorRef: ActorRef) extends GenericDataService[Book](storageActorRef)
  class SubscriberService(storageActorRef: ActorRef) extends GenericDataService[Subscriber](storageActorRef)
}


