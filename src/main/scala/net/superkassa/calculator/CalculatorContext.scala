package net.superkassa.calculator

import com.typesafe.config.Config
import net.superkassa.calculator.blacklist.BlacklistRule
import net.superkassa.calculator.rules.SegmentBonusRulesSerializer
import net.superkassa.models.Carrier
import net.superkassa.serialization
import org.joda.money.CurrencyUnit
import org.joda.time.DateTime
import org.json4s.Formats

case class CalculatorContext(
  now: DateTime,
  dataImage: DataImage
) {

}

object CalculatorContext {
  def apply(config: Config): CalculatorContext = {
    new CalculatorContext(
      new DateTime(),
      ???
    )
  }
}
