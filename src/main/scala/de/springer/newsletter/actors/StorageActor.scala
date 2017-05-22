package de.springer.newsletter.actors

import akka.actor.{Actor, Props}

import scala.reflect.ClassTag

class StorageActor[T: ClassTag] extends Actor {
  var items: Seq[T] = Nil

  import StorageActor.Messages._

  def receive: Receive = {
    case Store(item: T) =>
      items = items :+ item
      sender() ! Stored
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
    trait Stored
    object Stored extends Stored
  }

  def props[T: ClassTag] = Props(new StorageActor[T]())
}