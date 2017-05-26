package de.springer.newsletter

import de.springer.newsletter.models._
import de.springer.newsletter.stubs.StubData._

object Commons {
  val exampleNewsletters = Set(
    Newsletter(
      subA.email,
      Set(
        Notification(heatAndThermo.title, Set(Seq(science.code, physics.code, thermodynamics.code))),
        Notification(engAndThermo.title, Set(
          Seq(science.code, physics.code, thermodynamics.code),
          Seq(science.code, engineering.code))
        ),
        Notification(principlesOfAnalogElec.title, Set(Seq(science.code, engineering.code, electronics.code))),
        Notification(genPhysics.title, Set(Seq(science.code, physics.code))),
        Notification(mechSoftSys.title, Set(
          Seq(science.code, physics.code, mechanics.code),
          Seq(science.code, engineering.code, software.code),
          Seq(science.code, engineering.code, electronics.code))
        ),
        Notification(funcProgScala.title, Set(Seq(science.code, engineering.code, software.code, fp.code)))
      )
    ),
    Newsletter(
      subB.email,
      Set(
        Notification(heatAndThermo.title, Set(Seq(physics.code, thermodynamics.code))),
        Notification(engAndThermo.title, Set(Seq(physics.code, thermodynamics.code))),
        Notification(genPhysics.title, Set(Seq(physics.code))),
        Notification(mechSoftSys.title, Set(Seq(physics.code, mechanics.code))),
        Notification(funcProgScala.title, Set(Seq(fp.code)))
      )
    ),
    Newsletter(
      subC.email,
      Set(
        Notification(heatAndThermo.title, Set(Seq(thermodynamics.code))),
        Notification(engAndThermo.title, Set(Seq(thermodynamics.code))),
        Notification(mechSoftSys.title, Set(Seq(mechanics.code)))
      )
    ),
    Newsletter(
      subE.email,
      Set(
        Notification(principlesOfAnalogElec.title, Set(Seq(electronics.code))),
        Notification(mechSoftSys.title, Set(Seq(electronics.code)))
      )
    )
  )

  val exampleCategorizedBooksTrees = Set(
    CategorizedBooksTree(
      science, Set.empty, childTrees = Set(
        CategorizedBooksTree(
          physics, Set(genPhysics), childTrees = Set(
            CategorizedBooksTree(thermodynamics, Set(heatAndThermo, engAndThermo), Set.empty),
            CategorizedBooksTree(mechanics, Set(mechSoftSys), Set.empty)
          )
        ),
        CategorizedBooksTree(
          engineering, Set(engAndThermo), childTrees = Set(
            CategorizedBooksTree(electronics, Set(principlesOfAnalogElec, mechSoftSys), Set.empty),
            CategorizedBooksTree(
              software, Set(mechSoftSys), childTrees = Set(
                CategorizedBooksTree(fp, Set(funcProgScala), Set.empty),
                CategorizedBooksTree(oop, Set.empty, Set.empty)
              )
            )
          )
        )
      )
    )
  )
}
