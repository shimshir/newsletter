package de.springer.newsletter.services

import de.springer.newsletter.models._
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, SubscriberService}

import scala.annotation.tailrec
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class NewsletterService(categoryService: CategoryService, bookService: BookService, subscriberService: SubscriberService) {
  def createNewsletters(): Future[Set[Newsletter]] = {
    for {
      categories <- categoryService.list
      books <- bookService.list
      subscribers <- subscriberService.list
    } yield {
      NewsletterService.createNewsletters(categories.toSet, books.toSet, subscribers.toSet)
    }
  }
}

object NewsletterService {
  def createNewsletters(categories: Set[Category], books: Set[Book], subscribers: Set[Subscriber]): Set[Newsletter] = {
    val newsletters: Set[Newsletter] = subscribers map { subscriber =>
      val categoriesOfInterest = subscriber.categoryIds.flatMap(catId => catId2Cat(catId, categories))
      val rootBranches: Set[CategoryBranch] = categoriesOfInterest.map(Seq(_))
      val categoryBranches: Set[CategoryBranch] = createCategoryBranches(rootBranches, categories)

      val notifications: Set[Notification] = books map { book =>
        val bookCategories: Set[Category] = book.categoryIds.flatMap(catId => catId2Cat(catId, categories))
        val bookCatBranches: Set[CategoryBranch] = bookCategories flatMap { bookCat =>
          categoryBranches.filter(_.contains(bookCat)).map(_.takeWhile(_ != bookCat) :+ bookCat)
        }
        val categoryPaths = bookCatBranches.map(_.map(_.id))
        Notification(book.title, categoryPaths)
      }

      val nonEmptyNotifications = notifications.filter(_.categoryPaths.nonEmpty)
      Newsletter(subscriber.email, nonEmptyNotifications)
    }
    val nonEmptyNewsletters = newsletters.filter(_.notifications.nonEmpty)
    nonEmptyNewsletters
  }

  private def catId2Cat(catId: CategoryId, categories: Set[Category]): Option[Category] =
    categories.find(_.id == catId)

  @tailrec
  final def createCategoryBranches(branches: Set[CategoryBranch], categories: Set[Category]): Set[CategoryBranch] = {
    val newBranches: Set[CategoryBranch] = branches.flatMap(
      rootBranch => categories.filter(_.superCategoryId.contains(rootBranch.last.id)) match {
        case set if set.isEmpty => Set(rootBranch)
        case children => children.map(child => rootBranch :+ child)
      }
    )

    if (newBranches == branches)
      branches
    else
      createCategoryBranches(newBranches, categories)
  }
}
