package de.springer.newsletter.services

import akka.actor.{ActorRef, ActorSystem}
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.Category
import de.springer.newsletter.services.DataServices.CategoryService
import org.scalatest._


class CategoryServiceSpec extends FlatSpec with Matchers with DataServiceSpec {
  val actorSystem = ActorSystem("test-categoryService-system")
  implicit val ec = actorSystem.dispatcher

  "categoryService" should "be able to create and list categories" in {
    val categoryStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props[Category], "category-test-actor")
    val categoryService = new CategoryService(categoryStorageActorRef)
    val expectedCategories = (0 to 9).map(id => Category(id.toString, "title", None))
    writeAndRead(categoryService, expectedCategories.toSet) should contain only (expectedCategories: _*)
  }
}
