package net.superkassa.calculator

import java.sql.ResultSet

import net.superkassa.calculator.conditions.{Condition, MarketingCarrierCondition, OperatingCarrierCondition, ValidatingCarrierCondition}
import net.superkassa.calculator.rules._
import net.superkassa.models.enums.Gds
import net.superkassa.models.{Carrier, Itinerary, Recommendation}

package object blacklist {

  case class BlacklistRule(id: Int, reason: String, conditions: Iterable[Condition]) extends Rule

  object BlacklistRule {
    def apply(resultSet: ResultSet): BlacklistRule = {
      val id = resultSet.getInt("id")
      val reason = resultSet.getString("reason")
      val validatingCarrierCondition = ValidatingCarrierCondition(Set(Carrier(resultSet.getString("validated"))), Set())
      val marketingCarrierCondition = MarketingCarrierCondition(Set(Carrier(resultSet.getString("marketing"))), Set())
      BlacklistRule(id, reason)(validatingCarrierCondition, marketingCarrierCondition)
    }
  }

  implicit class BlacklistRules(blacklistRules: Iterable[BlacklistRule]) {
    def isMatching(calculationKey: CalculationKey): Boolean = {
      blacklistRules.exists { rule =>
        rule.isMatching(calculationKey)
      }
    }
  }

}
