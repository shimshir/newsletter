package de.springer.newsletter.services

import de.springer.newsletter.Commons
import de.springer.newsletter.models._
import de.springer.newsletter.services.DataServices.{BookService, CategoryService, SubscriberService}
import org.scalatest._
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


class NewsletterServiceSpec extends FlatSpec with Matchers with MockitoSugar with BeforeAndAfterAll {

  import de.springer.newsletter.stubs.StubData._

  val mockCategoryService = mock[CategoryService]
  val mockBookService = mock[BookService]
  val mockSubscriberService = mock[SubscriberService]

  override def beforeAll() {
    when(mockCategoryService.list).thenReturn(Future.successful(categories))
    when(mockBookService.list).thenReturn(Future.successful(books))
    when(mockSubscriberService.list).thenReturn(Future.successful(subscribers))
  }

  "createCategoryBranches" should "build category branches for provided root category" in {
    val initialScienceBranches: Set[CategoryBranch] = Set(Seq(science))
    val scienceBranches = NewsletterService.createCategoryBranches(initialScienceBranches, categories)

    val expectedBranchesForScience = Set(
      Seq(science, engineering, electronics),
      Seq(science, engineering, software, fp),
      Seq(science, engineering, software, oop),
      Seq(science, physics, mechanics),
      Seq(science, physics, thermodynamics)
    )

    scienceBranches shouldEqual expectedBranchesForScience
  }
  "createCategoryBranches" should "build category branches for a non root category" in {
    val initialEngineeringBranches: Set[CategoryBranch] = Set(Seq(engineering))
    val engineeringBranches = NewsletterService.createCategoryBranches(initialEngineeringBranches, categories)

    val expectedBranchesForEngineering = Set(
      Seq(engineering, electronics),
      Seq(engineering, software, fp),
      Seq(engineering, software, oop)
    )

    engineeringBranches shouldEqual expectedBranchesForEngineering
  }

  "createCategoryBranches" should "build category branches for different categories" in {
    val initialEngineeringAndPhysicsBranches: Set[CategoryBranch] = Set(Seq(engineering), Seq(physics))
    val engineeringAndPhysicsBranches = NewsletterService.createCategoryBranches(initialEngineeringAndPhysicsBranches, categories)

    val expectedBranchesForEngineeringAndPhysics = Set(
      Seq(engineering, electronics),
      Seq(engineering, software, fp),
      Seq(engineering, software, oop),
      Seq(physics, mechanics),
      Seq(physics, thermodynamics)
    )

    engineeringAndPhysicsBranches shouldEqual expectedBranchesForEngineeringAndPhysics
  }

  "createNewsletters" should "create a list of newsletters according to stored categories, books and subscribers" in {
    val newsletterService = new NewsletterService(mockCategoryService, mockBookService, mockSubscriberService)
    val newslettersFut = newsletterService.newsletters
    val newsletters = Await.result(newslettersFut, 1.second)
    newsletters shouldEqual Commons.exampleNewsletters
  }

  "createCategorizedBooksTrees" should "create a tree structure containing categories with books and subcategories" in {
    val newsletterService = new NewsletterService(mockCategoryService, mockBookService, mockSubscriberService)
    val categorizedBooksTreeFut = newsletterService.categorizedBooks
    val categorizedBooksTree = Await.result(categorizedBooksTreeFut, 1.second)
    categorizedBooksTree.childTrees shouldEqual Commons.exampleCategorizedBooksTrees
  }
}
