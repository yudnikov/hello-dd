package net.superkassa

import com.typesafe.config.ConfigFactory
import net.superkassa.calculator.CalculatorContext
import net.superkassa.calculator.conditions._
import net.superkassa.calculator.rules._
import net.superkassa.models._
import net.superkassa.models.enums._
import net.superkassa.models.primitives._
import net.superkassa.models.ranges.DateTimeRange
import org.joda.time.DateTime
import ru.yudnikov.clew._

object FlightBonusExample extends App {

  /*val context = CalculatorContext(ConfigFactory.load("publisher.conf"))

  val rule = FlightBonusRule(
    0, isDefault = true, None, None, ClearingCompany.Bsp, Market.Ru, DateTimeRange(None, None), PercentCalculation(), MoneyCalculation()
  )(
    BookingClassCondition(Set(), Set()), CabinClassCondition(Set(), Set()), DateCondition(DateTimeRange(None, None)), DateCondition(DateTimeRange(None, None)), ItineraryDateCondition(DateTimeRange(None, None)), InfantCondition(true)
  )(
    context
  )
  val in = FlightBonusRuleInput(
    Itinerary(), Recommendation(), SeatInfo(false), Carrier("SU"), new DateTime()
  )
  val res = rule.isMatching(in)*/

}
