package net.superkassa.calculator

import net.superkassa.models.geography._
import net.superkassa.models.primitives._
import net.superkassa.models._
import net.superkassa.models.ranges.DateTimeRange
import org.joda.time.DateTime

package object conditions {

  trait Condition {
    def predicate: CalculationKey => Boolean
    def isMatching(calculationKey: CalculationKey): Boolean = predicate(calculationKey)
  }

  // cake pattern
  trait LoggableCondition extends Condition {
    override def isMatching(calculationKey: CalculationKey): Boolean = {
      val result = super.isMatching(calculationKey)
      println(s"logging $result")
      result
    }
  }

  trait Inclusion[T] {
    val include: Set[T]
    def isIncluded(value: T): Boolean = include.contains(value)
  }

  trait Exclusion[T] {
    val exclude: Set[T]
    def isExcluded(value: T): Boolean = exclude.contains(value)
  }

  sealed trait InclusionType

  object InclusionType {
    case object Weak extends InclusionType
    case object Strict extends InclusionType
    case object Full extends InclusionType
  }

  trait GenericInclusionExclusion[T] extends Condition with Inclusion[T] with Exclusion[T] {
    val inclusionType: InclusionType
    def toValues: CalculationKey => Set[T]
    override def predicate: CalculationKey => Boolean = key => {
      val values = toValues(key)
      val isIncluded = inclusionType match {
        case InclusionType.Weak =>
          include.isEmpty || values.exists(include.contains)
        case InclusionType.Strict =>
          values.exists(include.contains)
        case InclusionType.Full =>
          values.forall(include.contains)
      }
      val nonExcluded = !values.exists(exclude.contains)
      isIncluded && nonExcluded
    }
  }

  case class BookingClassCondition(include: Set[BookingClass], exclude: Set[BookingClass], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[BookingClass] {
    override def toValues: CalculationKey => Set[BookingClass] = key => key.recommendation.bookingClasses
  }

  case class CabinClassCondition(include: Set[CabinClass], exclude: Set[CabinClass], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[CabinClass] {
    override def toValues: CalculationKey => Set[CabinClass] = calculationKey => Set(calculationKey.recommendation.cabinClass)
  }

  case class CurrentDateCondition(dateTimeRange: DateTimeRange) extends Condition {
    override def predicate: DateTime => Boolean = dateTime => dateTimeRange contains dateTime
  }

  case class DepartureDateCondition(dateTimeRange: DateTimeRange) extends Condition {
    override def predicate: Itinerary => Boolean = itinerary => dateTimeRange contains itinerary.departure.dateTime
  }

  case class DepartureCountryCondition(include: Set[Country], exclude: Set[Country], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[Country] {
    override def toValues: CalculationKey => Set[Country] = key => Set(key.itinerary.departure.country)
  }

  case class DepartureCityCondition(include: Set[City], exclude: Set[City], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[City] {
    override def toValues: Itinerary => Set[City] = itinerary => Set(itinerary.departure.city)
  }

  case class InfantCondition(isInfantAllowed: Boolean) extends Condition {
    override def predicate: SeatInfo => Boolean = seatInfo => seatInfo.isSeated == isInfantAllowed
  }

  case class ItineraryDateCondition(dateTimeRange: DateTimeRange) extends Condition {
    override def predicate: Itinerary => Boolean = itinerary => dateTimeRange contains itinerary.dateTimeRange
  }

  case class ValidatingCarrierCondition(include: Set[Carrier], exclude: Set[Carrier], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[Carrier] {
    override def toValues: Recommendation => Set[Carrier] = recommendation => recommendation.validatingCarriers
  }

  case class MarketingCarrierCondition(include: Set[Carrier], exclude: Set[Carrier], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[Carrier] {
    override def toValues: Itinerary => Set[Carrier] = itinerary => itinerary.marketingCarriers
  }

  case class OperatingCarrierCondition(include: Set[Carrier], exclude: Set[Carrier], inclusionType: InclusionType = InclusionType.Weak)
    extends GenericInclusionExclusion[Carrier] {
    override def toValues: Itinerary => Set[Carrier] = itinerary => itinerary.operatingCarriers
  }

}
