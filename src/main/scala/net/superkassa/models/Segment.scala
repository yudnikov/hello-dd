package net.superkassa.models

import net.superkassa.models.enums.Gds
import net.superkassa.models._
import scala.collection._

case class Segment(
  gds: Gds,
  flights: List[Flight],
  techStopsCount: Int
) {

  lazy val marketingCarriers: Set[Carrier] = flights.map(_.marketingCarrier)(breakOut[List[Flight], Carrier, Set[Carrier]])
  lazy val operatingCarriers: Set[Carrier] = flights.map(_.operatingCarrier)(breakOut[List[Flight], Carrier, Set[Carrier]])

}
