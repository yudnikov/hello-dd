package net.superkassa.calculator

import java.sql.ResultSet

import net.superkassa.datum.DatumContext
import net.superkassa.models.ranges.DateTimeRange
import org.joda.money._
import net.superkassa.models.enums._
import org.joda.time.DateTime
import ru.yudnikov.utils._

package object thresholds {

  sealed abstract class Realm(val code: String) extends Enum

  object Realm {
    case object IssuanceFare extends Realm("issuance-fare")
    case object IssuanceFlightBonus extends Realm("issuance-flight-bonus")
    case object IssuancePrice extends Realm("issuance-price")
    case object IssuanceClearing extends Realm("issuance-price-bsptch")
    def apply(string: String): Realm = string match {
      case "issuance-fare" => IssuanceFare
      case "issuance-flight-bonus" => IssuanceFlightBonus
      case "issuance-price" => IssuancePrice
      case "issuance-price-bsptch" => IssuanceClearing
    }
  }

  case class Threshold(
    realm: Realm,
    id: String,
    hits: Int,
    money: Money,
    maybeHitsThreshold: Option[Int],
    maybeValueThresholds: Option[Money],
    maybeLastNotified: Option[DateTime],
    dateTimeRange: DateTimeRange,
  )

  object Threshold {
    def apply(rs: ResultSet)(datumContext: DatumContext): Threshold = {
      val realm = Realm(rs.getString("realm"))
      val id = rs.getString("ident")
      val hits = rs.getInt("hits")
      val currencyUnit = Option(rs.getString("value_threshold_currency")).map(_.toCurrency).getOrElse(datumContext.defaultThresholdCurrencyUnit)
      val money = Money.of(currencyUnit, rs.getInt("value"))
      val maybeLastNotified = Option(rs.getDate("last_notified")).map(_.asJoda)
      val maybeHitsThreshold = Option(rs.getInt("hits_threshold"))
      val maybeMoneyThreshold = Option(rs.getInt("value_threshold")).map(Money.of(currencyUnit, _))
      val maybeStartDate = Option(rs.getDate("start_date")).map(_.asJoda)
      val maybeStopDate = Option(rs.getDate("stop_date")).map(_.asJoda)
      val range = DateTimeRange(maybeStartDate, maybeStopDate)
      new Threshold(realm, id, hits, money, maybeHitsThreshold, maybeMoneyThreshold, maybeLastNotified, range)
    }
  }

}
