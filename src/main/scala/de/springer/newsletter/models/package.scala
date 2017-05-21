package de.springer.newsletter

package object models {
  abstract class StringId {
    def value: String
  }
  trait MaybeIdentifiable[ID <: StringId] {
    def id: Option[ID]
    def withId(id: ID): MaybeIdentifiable[ID]
  }

  case class CategoryId(value: String) extends StringId
  case class Category(id: Option[CategoryId], title: String, superCategoryId: Option[CategoryId])
    extends MaybeIdentifiable[CategoryId] {
    def withId(id: CategoryId): MaybeIdentifiable[CategoryId] = this.copy(Some(id))
  }

  case class BookId(value: String) extends StringId
  case class Book(id: Option[BookId], title: String, categoryIds: Seq[CategoryId])
    extends MaybeIdentifiable[BookId] {
    def withId(id: BookId): MaybeIdentifiable[BookId] = this.copy(Some(id))
  }

  case class SubscriberId(value: String) extends StringId
  case class Subscriber(id: Option[SubscriberId], email: String, categoryIds: Seq[CategoryId])
    extends MaybeIdentifiable[SubscriberId] {
    def withId(id: SubscriberId): MaybeIdentifiable[SubscriberId] = this.copy(Some(id))
  }
}
