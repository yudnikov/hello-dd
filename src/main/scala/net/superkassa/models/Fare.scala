package net.superkassa.models

import net.superkassa.models.enums.TravellerType
import net.superkassa.models.primitives._

case class Fare(
  bookingClass: BookingClass,
  cabinSubclass: CabinSubclass,
  seatsAvailable: Int,
  fareType: FareType,
  rows: Map[TravellerType, FareRow]
)
