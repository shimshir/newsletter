package de.springer.newsletter.services

import com.typesafe.scalalogging.LazyLogging
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
      NewsletterService.createNewsletters(categories, books, subscribers)
    }
  }
}

object NewsletterService extends LazyLogging {
  def createNewsletters(categories: Set[Category], books: Set[Book], subscribers: Set[Subscriber]): Set[Newsletter] = {
    logger.info("Creating newsletters")
    val newsletters: Set[Newsletter] = subscribers map { subscriber =>
      val categoriesOfInterest = subscriber.categoryIds.flatMap(catId => catId2Cat(catId, categories))
      val rootBranches: Set[CategoryBranch] = categoriesOfInterest.map(Seq(_))

      logger.debug("Creating category branches")
      val categoryBranches: Set[CategoryBranch] = createCategoryBranches(rootBranches, categories)
      logger.debug(s"Got ${categoryBranches.size} branches")

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
    logger.info(s"Returning ${nonEmptyNewsletters.size} newsletters")
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

    if (newBranches == branches) {
      branches
    } else
      createCategoryBranches(newBranches, categories)
  }
}
