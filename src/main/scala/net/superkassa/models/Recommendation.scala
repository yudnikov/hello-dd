package net.superkassa.models

import net.superkassa.models.primitives.{BookingClass, CabinClass}
import org.joda.money.{CurrencyUnit, Money}
import ru.yudnikov.clew.PercentCalculation
import scala.collection.breakOut

case class Recommendation(
  segmentRecommendations: Map[String, SegmentRecommendation],
  ids: Map[String, Int],
  currencyUnit: CurrencyUnit,
  total: Money,
  farePrice: Money,
  seatInfos: List[SeatInfo],
  maybeBrokerPercent: Option[PercentCalculation] = None,
) {
  def seatsAvailableMatch(seats: Int): Boolean = {
    for {
      segmentRecommendation <- segmentRecommendations.values
      fare <- segmentRecommendation.fares
      if fare.seatsAvailable < seats
    } yield {
      return false
    }
    true
  }
  def seatsAvailableNotMatch(seats: Int): Boolean = !seatsAvailableMatch(seats)
  lazy val bookingClasses: Set[BookingClass] = {
    (for {
      segmentRecommendation <- segmentRecommendations.values
      fare <- segmentRecommendation.fares
    } yield fare.bookingClass)(scala.collection.breakOut[Iterable[SegmentRecommendation], BookingClass, Set[BookingClass]])
  }
  def cabinClass: CabinClass = CabinClass("TEST")
  lazy val validatingCarriers: Set[Carrier] = segmentRecommendations.values.flatMap(_.validatingCarriers)(breakOut[Iterable[SegmentRecommendation], Carrier, Set[Carrier]])
}
