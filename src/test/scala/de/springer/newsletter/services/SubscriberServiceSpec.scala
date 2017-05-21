package de.springer.newsletter.services

import akka.actor.{ActorRef, ActorSystem}
import de.springer.newsletter.actors.StorageActor
import de.springer.newsletter.models.{Subscriber, SubscriberId}
import de.springer.newsletter.services.DataServices.SubscriberService
import org.scalatest._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class SubscriberServiceSpec extends FlatSpec with Matchers {
  val actorSystem = ActorSystem("test-subscriberService-system")
  implicit val ec = actorSystem.dispatcher

  "create" should "create subscribers" in {
    val subscriberStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props(SubscriberId), "create-subscriber-test-actor")
    val subscriberService = new SubscriberService(subscriberStorageActorRef)
    val baseSub = Subscriber(None, "a@a.a", Nil)
    val subIdsFut = Future.sequence((0 to 9).map(_ => subscriberService.create(baseSub)))
    val subIds: Seq[SubscriberId] = Await.result(subIdsFut, 1.second)
    subIds should contain only((0 to 9).map(_.toString).map(SubscriberId): _*)
  }

  "list" should "return a list of all subscribers" in {
    val subscriberStorageActorRef: ActorRef = actorSystem.actorOf(StorageActor.props(SubscriberId), "list-subscriber-test-actor")
    val subscriberService = new SubscriberService(subscriberStorageActorRef)
    val baseSub = Subscriber(None, "a@a.a", Nil)
    val _ = Future.sequence((0 to 9).map(_ => subscriberService.create(baseSub)))
    val subsFut = subscriberService.list
    val subs: Seq[Subscriber] = Await.result(subsFut, 1.second)
    subs should contain only((0 to 9).map(_.toString).map(SubscriberId).map(baseSub.withId): _*)
  }
}
