package net.superkassa

import org.joda.money.CurrencyUnit
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.json4s.{CustomSerializer, DefaultFormats, Formats}
import org.json4s.JsonAST.JString

package object serialization {

  lazy val commonFormats: Formats = DefaultFormats + CurrencyUnitSerializer

  object CurrencyUnitSerializer extends CustomSerializer[CurrencyUnit](implicit formats => ( {
    case JString(s) => CurrencyUnit.of(s)
  }, {
    case currencyUnit: CurrencyUnit => JString(currencyUnit.getCode)
  }))

  case class DateTimeSerializer(formatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd"))
    extends CustomSerializer[DateTime](implicit formats => ( {
      case JString(str) =>
        formatter.parseDateTime(str)
    }, {
      case dateTime: DateTime =>
        JString(formatter.print(dateTime))
    }))

  object DateTimeSerializer {
    def apply(string: String): DateTimeSerializer = DateTimeSerializer(DateTimeFormat.forPattern(string))
  }

  val datumFormats: Formats = DefaultFormats

  val calculatorFormats: Formats = DefaultFormats

}
