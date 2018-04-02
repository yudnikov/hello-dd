package net.superkassa.models.ranges

import org.joda.time.DateTime

case class DateTimeRange(from: Option[DateTime], to: Option[DateTime]) extends GenericRange[DateTime] {
  override def toOrdered(value: DateTime): Ordered[DateTime] = date => value.getMillis compareTo date.getMillis
}
