package de.springer.newsletter.services

import de.springer.newsletter.models._
import org.scalatest._

class NewsletterServiceSpec extends FlatSpec with Matchers {
  val science = Category("science", "Science", None)
  val engineering = Category("engineering", "Engineering", Some("science"))
  val electronics = Category("electronics", "Electronics", Some("engineering"))
  val software = Category("software", "Software", Some("engineering"))
  val fp = Category("fp", "Functional Programming", Some("software"))
  val oop = Category("oop", "Object Oriented Programming", Some("software"))
  val physics = Category("physics", "Physics", Some("science"))
  val mechanics = Category("mechanics", "Mechanics", Some("physics"))
  val thermodynamics = Category("thermodynamics", "Thermodynamics", Some("physics"))

  val categories = Set(
    science,
    engineering,
    electronics,
    software,
    fp,
    oop,
    physics,
    mechanics,
    thermodynamics
  )

  val heatAndThermo = Book("1", "Heat and Thermodynamics", Set("thermodynamics"))
  val engAndThermo = Book("2", "Engineering and Thermodynamics", Set("thermodynamics", "engineering"))
  val principlesOfAnalogElec = Book("3", "Principles of Analog Electronics", Set("electronics"))
  val genPhysics = Book("4", "General Physics", Set("physics"))
  val mechSoftSys = Book("5", "Mechatronic Software Systems", Set("mechanics", "software", "electronics"))
  val funcProgScala = Book("6", "Functional Programming in Scala", Set("fp"))

  val books = Set(
    heatAndThermo,
    engAndThermo,
    principlesOfAnalogElec,
    genPhysics,
    mechSoftSys,
    funcProgScala
  )

  val subA = Subscriber("1", "a@a.a", Set("science"))
  val subB = Subscriber("2", "b@b.b", Set("physics", "fp"))
  val subC = Subscriber("3", "c@c.c", Set("thermodynamics", "mechanics"))
  val subD = Subscriber("4", "d@d.d", Set("oop"))
  val subE = Subscriber("5", "e@e.e", Set("electronics"))

  val subscribers = Set(
    subA,
    subB,
    subC,
    subD,
    subE
  )

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
    val newsletters = NewsletterService.createNewsletters(categories, books, subscribers)
    newsletters shouldEqual expectedNewsletters
  }

  val expectedNewsletters = Set(
    Newsletter(
      subA.email,
      Set(
        Notification(heatAndThermo.title, Set(Seq(science.id, physics.id, thermodynamics.id))),
        Notification(engAndThermo.title, Set(
          Seq(science.id, physics.id, thermodynamics.id),
          Seq(science.id, engineering.id))
        ),
        Notification(principlesOfAnalogElec.title, Set(Seq(science.id, engineering.id, electronics.id))),
        Notification(genPhysics.title, Set(Seq(science.id, physics.id))),
        Notification(mechSoftSys.title, Set(
          Seq(science.id, physics.id, mechanics.id),
          Seq(science.id, engineering.id, software.id),
          Seq(science.id, engineering.id, electronics.id))
        ),
        Notification(funcProgScala.title, Set(Seq(science.id, engineering.id, software.id, fp.id)))
      )
    ),
    Newsletter(
      subB.email,
      Set(
        Notification(heatAndThermo.title, Set(Seq(physics.id, thermodynamics.id))),
        Notification(engAndThermo.title, Set(Seq(physics.id, thermodynamics.id))),
        Notification(genPhysics.title, Set(Seq(physics.id))),
        Notification(mechSoftSys.title, Set(Seq(physics.id, mechanics.id))),
        Notification(funcProgScala.title, Set(Seq(fp.id)))
      )
    ),
    Newsletter(
      subC.email,
      Set(
        Notification(heatAndThermo.title, Set(Seq(thermodynamics.id))),
        Notification(engAndThermo.title, Set(Seq(thermodynamics.id))),
        Notification(mechSoftSys.title, Set(Seq(mechanics.id)))
      )
    ),
    Newsletter(
      subE.email,
      Set(
        Notification(principlesOfAnalogElec.title, Set(Seq(electronics.id))),
        Notification(mechSoftSys.title, Set(Seq(electronics.id)))
      )
    )
  )
}
