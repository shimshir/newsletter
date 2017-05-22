package de.springer.newsletter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

package object models {
  type CategoryId = String
  type BookId = String
  type SubscriberId = String

  type CategoryBranch = Seq[Category]

  case class Category(id: CategoryId, title: String, superCategoryId: Option[CategoryId])

  case class Book(id: BookId, title: String, categoryIds: Set[CategoryId])

  case class Subscriber(id: SubscriberId, email: String, categoryIds: Set[CategoryId])

  case class Notification(bookTitle: String, categoryPaths: Set[Seq[CategoryId]])
  case class Newsletter(email: String, notifications: Set[Notification])

  object JsonProtocols extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val categoryFormat: RootJsonFormat[Category] = jsonFormat3(Category)
    implicit val bookFormat: RootJsonFormat[Book] = jsonFormat3(Book)
    implicit val subscriberFormat: RootJsonFormat[Subscriber] = jsonFormat3(Subscriber)

    implicit val notificationFormat: RootJsonFormat[Notification] = jsonFormat2(Notification)
    implicit val newsletterFormat: RootJsonFormat[Newsletter] = jsonFormat2(Newsletter)
  }
}
