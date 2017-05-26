package de.springer.newsletter.services

import com.typesafe.scalalogging.LazyLogging
import de.springer.newsletter.models._
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, SubscriberService}

import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}

class NewsletterService(categoryService: CategoryService,
                        bookService: BookService,
                        subscriberService: SubscriberService)
                       (implicit ec: ExecutionContext) {
  def newsletters: Future[Set[Newsletter]] = {
    for {
      categories <- categoryService.list
      books <- bookService.list
      subscribers <- subscriberService.list
    } yield {
      NewsletterService.createNewsletters(categories, books, subscribers)
    }
  }

  def categorizedBooks: Future[CategorizedBooksTree] = {
    for (categories <- categoryService.list; books <- bookService.list) yield {
      NewsletterService.createCategorizedBooksTree(categories, books)
    }
  }
}

object NewsletterService extends LazyLogging {
  def createNewsletters(categories: Set[Category], books: Set[Book], subscribers: Set[Subscriber]): Set[Newsletter] = {
    logger.info("Creating newsletters")
    val newsletters: Set[Newsletter] = subscribers map { subscriber =>
      val categoriesOfInterest = subscriber.categoryCodes.flatMap(catId => catId2Cat(catId, categories))
      val rootBranches: Set[CategoryBranch] = categoriesOfInterest.map(Seq(_))

      logger.debug("Creating category branches")
      val categoryBranches: Set[CategoryBranch] = createCategoryBranches(rootBranches, categories)
      logger.debug(s"Got ${categoryBranches.size} branches")

      val notifications: Set[Notification] = books map { book =>
        val bookCategories: Set[Category] = book.categoryCodes.flatMap(catId => catId2Cat(catId, categories))
        val bookCatBranches: Set[CategoryBranch] = bookCategories flatMap { bookCat =>
          categoryBranches.filter(_.contains(bookCat)).map(_.takeWhile(_ != bookCat) :+ bookCat)
        }
        val categoryPaths = bookCatBranches.map(_.map(_.code))
        Notification(book.title, categoryPaths)
      }

      val nonEmptyNotifications = notifications.filter(_.categoryPaths.nonEmpty)
      Newsletter(subscriber.email, nonEmptyNotifications)
    }
    val nonEmptyNewsletters = newsletters.filter(_.notifications.nonEmpty)
    logger.info(s"Returning ${nonEmptyNewsletters.size} newsletters")
    nonEmptyNewsletters
  }

  def createCategorizedBooksTree(categories: Set[Category], books: Set[Book]): CategorizedBooksTree = {
    val categorizedBooksTrees: Set[CategorizedBooksTree] = categories.filter(_.superCategoryCode.isEmpty) map { cat =>
      CategorizedBooksTree(cat, booksInCat(cat, books), childTrees(cat, categories, books))
    }

    val rootTree = CategorizedBooksTree(childTrees = categorizedBooksTrees)
    logger.info(s"Returning categorized books")
    rootTree
  }

  private def childTrees(parentCategory: Category, categories: Set[Category], books: Set[Book]): Set[CategorizedBooksTree] = {
    categories.partition(_.superCategoryCode.contains(parentCategory.code)) match {
      case (children, _) if children.isEmpty =>
        logger.debug(s"'${parentCategory.code}' has no child categories, returning empty set")
        Set.empty
      case (children, others) =>
        children.map(childCat => CategorizedBooksTree(childCat, booksInCat(childCat, books), childTrees(childCat, others, books)))
    }
  }

  private def booksInCat(cat: Category, books: Set[Book]) =
    books.filter(_.categoryCodes.contains(cat.code))

  private def catId2Cat(catId: CategoryCode, categories: Set[Category]): Option[Category] =
    categories.find(_.code == catId)

  @tailrec
  final def createCategoryBranches(branches: Set[CategoryBranch], categories: Set[Category]): Set[CategoryBranch] = {
    val newBranches: Set[CategoryBranch] = branches.flatMap(
      rootBranch => categories.filter(_.superCategoryCode.contains(rootBranch.last.code)) match {
        case set if set.isEmpty => Set(rootBranch)
        case children => children.map(child => rootBranch :+ child)
      }
    )

    if (newBranches == branches) {
      branches
    } else {
      createCategoryBranches(newBranches, categories)
    }
  }
}
