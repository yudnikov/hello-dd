package net.superkassa

import net.superkassa.models._
import net.superkassa.models.enums.{Gds, PaymentType, TravellerType}
import ru.yudnikov.clew.MoneyCalculation
import net.superkassa.calculator.blacklist._
import org.joda.time.DateTime

package object calculator {

  case class CalculationKey(now: DateTime, gds: Gds, itinerary: Itinerary, recommendation: Recommendation, maybeCarrier: Option[Carrier] = None, maybeSeatInfo: Option[SeatInfo] = None)

  case class DerivationForSearch(gds: Gds, carrier: Carrier, profitForbidden: Boolean, desired: Map[PaymentType, Desired])

  object DerivationForSearch {
    def apply(
      gds: Gds,
      paymentTypes: Set[PaymentType],
      searchResult: SearchResult,
      travellers: Map[TravellerType, Int],
      agencyGroupId: Int,
      isDealer: Boolean = false,
      isPossum: Boolean = false,
      isMetaSearch: Boolean = false,
      isDirectPaymentAllowed: Boolean = false,
      isFilterApply: Boolean = false
    )(
      calculatorContext: CalculatorContext
    ): DerivationForSearch = {

      def isNotFiltered(tuple: (Recommendation, Itinerary)): Boolean = !isFiltered(tuple._1, tuple._2)

      def isFiltered(recommendation: Recommendation, itinerary: Itinerary): Boolean = {
        import calculatorContext._
        isFilterApply && {
          {
            now.plusHours(gds.minDepartureTime.getHours) isAfter itinerary.departure.dateTime
          } || {
            recommendation seatsAvailableNotMatch recommendation.seatInfos.count(_.isSeated)
          } || {
            dataImage.blacklistRules.isMatching(CalculationKey(calculatorContext.now, gds, itinerary, recommendation))
          }
        }
      }

      searchResult.data.filter(isNotFiltered) map { case (recommendation, itinerary) =>
        Carrier.perStock(gds, recommendation.validatingCarriers) map { case (stock, carriers) =>
          carriers map { carrier =>
            // GDS COMMISSION
            ???
          }
        }
      }
      ???
    }
  }

  case class Desired(
    isDirectPayment: Boolean,
    price: MoneyCalculation,
    profit: MoneyCalculation,
    sum: MoneyCalculation
  )

}
