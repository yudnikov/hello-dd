package net.superkassa.models

import net.superkassa.models.geography.{City, Country, Port, Region}
import org.joda.time.DateTime

case class RoutePoint(port: Port, maybeTerminalNumber: Option[Int], dateTime: DateTime) {
  lazy val city: City = port.city
  lazy val country: Country = port.country
  lazy val region: Region = port.region
}
