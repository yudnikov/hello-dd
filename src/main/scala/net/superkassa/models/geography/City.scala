package net.superkassa.models.geography

import akka.cluster.ddata.ReplicatedData

case class City(code: String) extends Location {
  override def contains(location: Location)(implicit geography: Geography): Boolean = ???
}

