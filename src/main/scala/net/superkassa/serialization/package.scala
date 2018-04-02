package net.superkassa

import org.joda.money.CurrencyUnit
import org.json4s.{CustomSerializer, DefaultFormats, Formats}
import org.json4s.JsonAST.JString

package object serialization {

  val commonFormats: Formats = DefaultFormats + CurrencyUnitSerializer

  object CurrencyUnitSerializer extends CustomSerializer[CurrencyUnit](implicit formats => ({
    case JString(s) => CurrencyUnit.of(s)
  }, {
    case currencyUnit: CurrencyUnit => JString(currencyUnit.getCode)
  }))

  val datumFormats: Formats = ???

  val calculatorFormats: Formats = ???

}
