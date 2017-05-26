package de.springer.newsletter.stubs

import de.springer.newsletter.models.{Book, Category, Subscriber}

object StubData {
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

  val heatAndThermo = Book("Heat and Thermodynamics", Set("thermodynamics"))
  val engAndThermo = Book("Engineering and Thermodynamics", Set("thermodynamics", "engineering"))
  val principlesOfAnalogElec = Book("Principles of Analog Electronics", Set("electronics"))
  val genPhysics = Book("General Physics", Set("physics"))
  val mechSoftSys = Book("Mechatronic Software Systems", Set("mechanics", "software", "electronics"))
  val funcProgScala = Book("Functional Programming in Scala", Set("fp"))

  val books = Set(
    heatAndThermo,
    engAndThermo,
    principlesOfAnalogElec,
    genPhysics,
    mechSoftSys,
    funcProgScala
  )

  val subA = Subscriber("a@a.a", Set("science"))
  val subB = Subscriber("b@b.b", Set("physics", "fp"))
  val subC = Subscriber("c@c.c", Set("thermodynamics", "mechanics"))
  val subD = Subscriber("d@d.d", Set("oop"))
  val subE = Subscriber("e@e.e", Set("electronics"))

  val subscribers = Set(
    subA,
    subB,
    subC,
    subD,
    subE
  )
}
