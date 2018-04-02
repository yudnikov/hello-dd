package net.superkassa.datum

import com.typesafe.config.Config
import net.superkassa.calculator.rules.SegmentBonusRulesSerializer
import net.superkassa.models.Carrier
import net.superkassa.serialization
import org.joda.money.CurrencyUnit
import org.json4s.Formats

case class DatumContext(
  defaultCurrencyUnit: CurrencyUnit,
  defaultSegmentBonusCurrencyUnit: CurrencyUnit,
  defaultThresholdCurrencyUnit: CurrencyUnit,
) {
  def segmentBonusRulesFormats(carrier: Carrier): Formats = serialization.commonFormats + SegmentBonusRulesSerializer(this, carrier)
}

object DatumContext {
  def apply(config: Config): DatumContext = {
    val defaultCurrencyUnit = CurrencyUnit.of(config.getString("superkassa.calculator.defaultCurrencyUnit"))
    val defaultSegmentBonusCurrencyUnit = CurrencyUnit.of(config.getString("superkassa.calculator.defaultSegmentBonusCurrencyUnit"))
    val defaultThresholdCurrencyUnit = CurrencyUnit.of(config.getString("superkassa.calculator.defaultThresholdCurrencyUnit"))
    DatumContext(defaultCurrencyUnit, defaultSegmentBonusCurrencyUnit, defaultThresholdCurrencyUnit)
  }
}
