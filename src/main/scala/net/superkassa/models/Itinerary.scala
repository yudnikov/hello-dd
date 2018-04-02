package net.superkassa.models

import net.superkassa.models.ranges.DateTimeRange

case class Itinerary(segments: Iterable[Segment]) {
  lazy val dateTimeRange: DateTimeRange = ???
  lazy val departure: RoutePoint = ???
  lazy val arrival: RoutePoint = ???
  lazy val marketingCarriers: Set[Carrier] = segments.flatMap(_.marketingCarriers).toSet
  lazy val operatingCarriers: Set[Carrier] = segments.flatMap(_.operatingCarriers).toSet
}

object Itinerary {

}