package de.springer.newsletter.services

import de.springer.newsletter.services.DataServices.GenericDataService

import scala.concurrent.duration._
import scala.concurrent.Await

trait DataServiceSpec {
  def writeAndRead[S <: GenericDataService[T], T](dataService: S, itemsToStore: Seq[T]): Seq[T] = {
    val _ = itemsToStore.map(item => dataService.create(item))
    val items: Seq[T] = Await.result(dataService.list, 1.second)
    items
  }
}
