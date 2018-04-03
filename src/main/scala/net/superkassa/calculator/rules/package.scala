package net.superkassa.calculator

import java.sql.ResultSet

import net.superkassa.calculator.conditions._
import net.superkassa.datum.DatumContext
import net.superkassa.models.{Carrier, Itinerary, Recommendation, SeatInfo}
import net.superkassa.models.enums._
import net.superkassa.models.primitives.SourceDocument
import net.superkassa.models.ranges.DateTimeRange
import net.superkassa.serialization
import net.superkassa.serialization.DateTimeSerializer
import org.joda.money.{CurrencyUnit, Money}
import org.joda.time.DateTime
import org.json4s.JsonAST.{JNull, JObject, JString, JValue}
import org.json4s.{CustomSerializer, Formats}
import org.json4s.jackson.Serialization
import ru.yudnikov.clew.{MoneyCalculation, PercentCalculation}

package object rules {

  trait Rule {
    def isMatching(calculationKey: CalculationKey): Boolean
  }

  trait ConditionedRule extends Rule {
    def conditions: Iterable[Condition]
    override def isMatching(calculationKey: CalculationKey): Boolean = conditions.forall(_.isMatching(calculationKey))
  }

  // FLIGHT BONUS

  case class FlightBonusRule(
    id: Int,
    isDefault: Boolean,
    alternativeId: Option[String],
    maybeIsDefaultAlternative: Option[Boolean],
    clearingCompany: ClearingCompany,
    market: Market,
    dateTimeRange: DateTimeRange,
    percent: PercentCalculation,
    money: MoneyCalculation,
    conditions: Iterable[Condition],
  )(
    calculatorContext: CalculatorContext,
  ) extends Rule {
    override def isMatching(calculationKey: CalculationKey): Boolean = {
      conditions.forall(_.isMatching(calculationKey))
    }
  }

  // SEGMENT BONUS

  case class SegmentBonusRule(carrier: Carrier, gds: Gds, isDefault: Boolean, isTaxed: Boolean, isLrOnly: Boolean, maybeMoney: Option[Money])

  object SegmentBonusRule {
    def apply(rs: ResultSet, datumContext: DatumContext): Set[SegmentBonusRule] = {
      val carrier = Carrier(rs.getString("validating_carrier"))
      val json = rs.getString("conditions")
      implicit val formats: Formats = datumContext.segmentBonusRulesFormats(carrier)
      Serialization.read[Set[SegmentBonusRule]](json)
    }
  }

  case class SegmentBonusRulesSerializer(datumContext: DatumContext, carrier: Carrier) extends CustomSerializer[Set[SegmentBonusRule]](implicit formats => ({
    case JObject(jFields) =>
      val fields = jFields.toMap
      def extract(gds: Gds, restFields: Map[String, JValue]): SegmentBonusRule = {
        val isDefault = restFields.get("default").exists(_.extract[Boolean])
        val isTaxed = restFields.get("apply_vat").exists(_.extract[Boolean])
        val isLrOnly = restFields.get("only_lr").exists(_.extract[Boolean])
        val maybeMoney = {
          val maybeAmount = restFields.get("absolute_bonus").map(_.extract[Double])
          val currencyUnit = restFields.get("absolute_bonus_currency").map(_.extract[CurrencyUnit]).getOrElse(datumContext.defaultSegmentBonusCurrencyUnit)
          maybeAmount.map(Money.of(currencyUnit, _))
        }
        SegmentBonusRule(carrier, gds, isDefault, isTaxed, isLrOnly, maybeMoney)
      }
      fields.collect {
        case (Gds(gds), JObject(restFields)) =>
          extract(gds, restFields.toMap)
      }.toSet
  }, {
    case _ => JNull
  }))

  // CLEARING

  trait ClearingCommission {
    def calculate(farePrice: Money, segmentsNumber: Int): MoneyCalculation
  }

  case class PercentClearingCommission(percent: BigDecimal) extends ClearingCommission {
    override def calculate(farePrice: Money, segmentsNumber: Int = 1): MoneyCalculation = ???
  }

  case class FixedClearingCommission(money: Money) extends ClearingCommission {
    override def calculate(farePrice: Money, segmentsNumber: Int = 1): MoneyCalculation = ???
  }

  case class FixedMoneyPerSegment(money: Money) extends ClearingCommission {
    override def calculate(farePrice: Money, segmentsNumber: Int): MoneyCalculation = ???
  }

  object ClearingCommission {
    def apply(rs: ResultSet)(datumContext: DatumContext): ClearingCommission = {
      import datumContext._
      val commissionType = rs.getString("commission_type")
      val amount = rs.getDouble("commission_value")
      val currencyUnitMask = "[A-Z]{3}".r
      val currencyUnitPerSegment = "[A-Z]{3}#".r
      commissionType match {
        case "%" =>
          PercentClearingCommission(amount)
        case code@currencyUnitMask() if currencyUnits.contains(code) =>
          val money = Money.of(CurrencyUnit.of(code), amount)
          FixedClearingCommission(money)
        case code@currencyUnitPerSegment() if currencyUnits.contains(code.take(3)) =>
          val money = Money.of(CurrencyUnit.of(code.take(3)), amount)
          FixedMoneyPerSegment(money)
        case x =>
          throw new Exception(s"can't extract ClearingCommission from $x")
      }
    }
  }

  trait ClearingRule extends Rule {
    val id: Int
    val market: Market
    val shop: Shop
    val clearingCompany: ClearingCompany
    val carrier: Carrier
    val paymentSchemes: Set[PaymentScheme]
    val clearingCommission: ClearingCommission
  }

  case class BspRule(
    id: Int,
    market: Market,
    shop: Shop,
    carrier: Carrier,
    clearingCommission: ClearingCommission,
    conditions: Iterable[Condition],
  ) extends ClearingRule {
    val paymentSchemes: Set[PaymentScheme] = Set()
    override val clearingCompany: ClearingCompany = ClearingCompany.Bsp
    override def isMatching(calculationKey: CalculationKey): Boolean = ???
  }

  object BspRule {
    def apply(rs: ResultSet)(datumContext: DatumContext): BspRule = {
      val market = Market(rs.getString("market"))
      val shop = Shop(rs.getString("shop"))
      val carrier = Carrier(rs.getString("validating_carrier"))
      val id = rs.getInt("id")
      val clearingCommission = ClearingCommission(rs)(datumContext)
      val json = rs.getString("conditions")

      BspRule(id, market, shop, carrier, clearingCommission, Iterable.empty)
    }
  }

  case class TchRule(
    id: Int,
    market: Market,
    shop: Shop,
    sourceDate: DateTime,
    sourceDocument: SourceDocument,
    carrier: Carrier,
    paymentSchemes: Set[PaymentScheme],
    clearingCommission: ClearingCommission,
    conditions: Iterable[Condition]
  ) extends ClearingRule
    with Ordered[TchRule] {

    override val clearingCompany: ClearingCompany = ClearingCompany.Tch

    override def isMatching(calculationKey: CalculationKey): Boolean = {
      ???
    }
    override def compare(that: TchRule): Int = {
      sourceDate compareTo that.sourceDate match {
        case 0 =>
          sourceDocument.number compareTo that.sourceDocument.number
        case i =>
          i
      }
    }
  }

  object TchRule {
    def apply(rs: ResultSet)(datumContext: DatumContext): TchRule = {
      val market = Market(rs.getString("market"))
      val shop = Shop(rs.getString("shop"))
      val carrier = Carrier(rs.getString("validating_carrier"))
      val id = rs.getInt("id")
      val clearingCommission = ClearingCommission(rs)(datumContext)
      val json = rs.getString("conditions")
      implicit val formats: Formats = serialization.commonFormats + TchRuleSerializer(id, carrier, shop, market, clearingCommission)
      Serialization.read[TchRule](json)
    }
  }

  case class TchRuleSerializer(id: Int, carrier: Carrier, shop: Shop, market: Market, clearingCommission: ClearingCommission)
    extends CustomSerializer[TchRule](formats => ({
      case JObject(jFields) =>
        val fields = jFields.toMap
        implicit val formatsWithDateTime: Formats = formats + DateTimeSerializer("yyyy-MM-dd")
        require(fields.contains("source_date") && fields.contains("source_document"), "source_date & source_document should be defined!")
        val sourceDate = fields("source_date").extract[DateTime]
        val sourceDocument = SourceDocument(fields("source_document").extract[String])
        val paymentSchemes = fields.get("payment_scheme").map(_.extract[Set[String]].map(PaymentScheme(_))).getOrElse(Set())
        new TchRule(id, market, shop, sourceDate, sourceDocument, carrier, paymentSchemes, clearingCommission, Iterable.empty)
  }, {
    case _ => JNull
  }))

  // GDS

  case class GdsRule(

  )

  case class PaymentTypeRule(

  )

  case class SourceRule(

  )

}
