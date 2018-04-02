package net.superkassa.models

import net.superkassa.models.geography.{Country, Region}

case class Flight(
  departure: RoutePoint,
  arrival: RoutePoint,
  techStops: List[TechStop],
  marketingCarrier: Carrier,
  operatingCarrier: Carrier,
  flightNumber: String,
  planeNumber: String
) {
  lazy val countries: Set[Country] = Set(departure.country, arrival.country)
  lazy val regions: Set[Region] = Set(departure.region, arrival.region)
  lazy val marketingToOperatingCarriers: (Carrier, Carrier) = marketingCarrier -> operatingCarrier
}
