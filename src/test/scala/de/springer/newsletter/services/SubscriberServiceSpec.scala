package de.springer.newsletter.services

import akka.actor.{ActorRef, ActorSystem}
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.Subscriber
import de.springer.newsletter.services.DataServices.SubscriberService
import org.scalatest._

class SubscriberServiceSpec extends FlatSpec with Matchers with DataServiceSpec {
  val actorSystem = ActorSystem("test-subscriberService-system")
  implicit val ec = actorSystem.dispatcher

  "subscriberService" should "be able to create and list subscribers" in {
    val subscriberStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props[Subscriber], "subscriber-test-actor")
    val subscriberService = new SubscriberService(subscriberStorageActorRef)
    val expectedSubscribers = (0 to 9).map(id => Subscriber(id.toString, "title", Set.empty))
    writeAndRead(subscriberService, expectedSubscribers.toSet) should contain only (expectedSubscribers: _*)
  }
}
