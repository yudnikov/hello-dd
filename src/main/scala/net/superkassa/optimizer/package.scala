package net.superkassa

import net.superkassa.models.Carrier
import net.superkassa.models.enums.Gds
import org.joda.time.DateTime
import net.superkassa.models.enums._
import net.superkassa.models.primitives.Partner
import net.superkassa.models.ranges.DateTimeRange
import org.joda.money.Money

package object optimizer {

  sealed class OptimizerEventType(val code: String) extends Enum

  case class OptimizerEvent(
    dateTime: DateTime,
    eventType: OptimizerEventType,
    gds: Gds,
    carrier: Carrier,
    partner: Partner,
    segmentsAmount: Int,
    price: Money,
    profit: Money,
  )

  // event
  case class Target(
    dateTimeRange: DateTimeRange,
    maybeGds: Option[Gds],
    maybeCarrier: Option[Carrier],
    maybePartner: Option[Partner],
  )

}
