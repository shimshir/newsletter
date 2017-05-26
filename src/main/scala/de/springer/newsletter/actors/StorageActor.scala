package de.springer.newsletter.actors

import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.Logger

import scala.reflect.ClassTag

class StorageActor[T: ClassTag] extends Actor {
  val logger = Logger(s"${getClass.getName}[${implicitly[ClassTag[T]].runtimeClass.getSimpleName}]")

  var items: Set[T] = Set.empty

  import StorageActor.Messages._

  def receive: Receive = {
    case msg@Store(item: T) =>
      logger.trace(s"Received message: $msg")
      items = items + item
      sender() ! Stored
    case List =>
      logger.trace("Received message: List")
      sender() ! items
  }

  override def unhandled(message: Any): Unit = {
    logger.error(s"Could not handle message: $message")
  }
}

object StorageActor {
  object Messages {
    case class Store[T](item: T)
    object List
    trait Stored
    object Stored extends Stored
  }

  def props[T: ClassTag]: Props = Props(new StorageActor[T]())
}
