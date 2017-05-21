package de.springer.newsletter.actors

import akka.actor.{Actor, Props}
import de.springer.newsletter.models.{MaybeIdentifiable, StringId}

class StorageActor[ID <: StringId](createStringId: String => ID) extends Actor {
  var items: Seq[MaybeIdentifiable[ID]] = Nil

  import StorageActor.Messages._

  def receive: Receive = {
    case Store(item: MaybeIdentifiable[ID]) =>
      val id = createStringId(items.size.toString)
      items = items :+ item.withId(id)
      sender() ! id
    case List =>
      sender() ! items
  }

  override def unhandled(message: Any): Unit = {
    println(s"Could not handle message: $message")
  }
}

object StorageActor {
  object Messages {
    case class Store[T](item: T)
    object List
  }

  def props[ID <: StringId](createStringId: String => ID) = Props(new StorageActor[ID](createStringId))
}