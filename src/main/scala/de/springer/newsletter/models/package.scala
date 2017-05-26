package de.springer.newsletter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

package object models {
  type CategoryCode = String
  type BookId = String
  type SubscriberId = String

  type CategoryBranch = Seq[Category]

  case class Category(code: CategoryCode, title: String, superCategoryCode: Option[CategoryCode])

  val rootCategory = Category("root", "Root", None)

  case class Book(title: String, categoryCodes: Set[CategoryCode])

  case class Subscriber(email: String, categoryCodes: Set[CategoryCode])

  case class Notification(bookTitle: String, categoryPaths: Set[Seq[CategoryCode]])
  case class Newsletter(email: String, notifications: Set[Notification])

  case class CategorizedBooksTree(category: Category = rootCategory, books: Set[Book] = Set.empty, childTrees: Set[CategorizedBooksTree])

  object JsonProtocols extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val categoryFormat: RootJsonFormat[Category] = jsonFormat3(Category)
    implicit val bookFormat: RootJsonFormat[Book] = jsonFormat2(Book)
    implicit val subscriberFormat: RootJsonFormat[Subscriber] = jsonFormat2(Subscriber)

    implicit val notificationFormat: RootJsonFormat[Notification] = jsonFormat2(Notification)
    implicit val newsletterFormat: RootJsonFormat[Newsletter] = jsonFormat2(Newsletter)
    implicit val categorizedBooksTreeFormat: RootJsonFormat[CategorizedBooksTree] = new RootJsonFormat[CategorizedBooksTree] {
      override def write(tree: CategorizedBooksTree): JsValue = {
        JsObject(
          "category" -> tree.category.toJson,
          "books" -> tree.books.toJson,
          "childTrees" -> tree.childTrees.map(this.write).toJson
        )
      }

      override def read(json: JsValue): CategorizedBooksTree = jsonFormat3(CategorizedBooksTree).read(json)
    }
  }
}
