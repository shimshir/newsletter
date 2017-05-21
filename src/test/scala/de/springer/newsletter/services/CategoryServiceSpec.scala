package de.springer.newsletter.services

import akka.actor.{ActorRef, ActorSystem}
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.{Category, CategoryId}
import de.springer.newsletter.services.DataServices.CategoryService
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class CategoryServiceSpec extends FlatSpec with Matchers {
  val actorSystem = ActorSystem("test-categoryService-system")
  implicit val ec = actorSystem.dispatcher

  "create" should "create categories" in {
    val categoryStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props(CategoryId), "create-category-test-actor")
    val categoryService = new CategoryService(categoryStorageActorRef)
    val baseCat = Category(None, "title", None)
    val catIdsFut = Future.sequence((0 to 9).map(_ => categoryService.create(baseCat)))
    val catIds: Seq[CategoryId] = Await.result(catIdsFut, 1.second)
    catIds should contain only((0 to 9).map(_.toString).map(CategoryId): _*)
  }

  "list" should "return a list of all categories" in {
    val categoryStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props(CategoryId), "list-category-test-actor")
    val categoryService = new CategoryService(categoryStorageActorRef)
    val baseCat = Category(None, "title", None)
    val _ = Future.sequence((0 to 9).map(_ => categoryService.create(baseCat)))
    val catsFut = categoryService.list
    val cats: Seq[Category] = Await.result(catsFut, 1.second)
    cats should contain only((0 to 9).map(_.toString).map(CategoryId).map(baseCat.withId): _*)
  }
}
