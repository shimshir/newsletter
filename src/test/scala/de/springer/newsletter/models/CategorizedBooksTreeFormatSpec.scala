package de.springer.newsletter.models

import de.springer.newsletter.Commons
import org.scalatest._
import de.springer.newsletter.models.JsonProtocols._
import spray.json._


class CategorizedBooksTreeFormatSpec extends FlatSpec with Matchers {
  "categorizedBooksTreeFormat" should "correctly serialize and deserialize CategorizedBooksTree to/from json" in {

    val serializedCategorizedBooksTrees = Commons.exampleCategorizedBooksTrees.map(categorizedBooksTreeFormat.write).toJson
    val deserializedCategorizedBooksTrees = serializedCategorizedBooksTrees.convertTo[Set[CategorizedBooksTree]]

    deserializedCategorizedBooksTrees shouldEqual Commons.exampleCategorizedBooksTrees
  }
}
