package net.superkassa

import java.util.concurrent.ThreadLocalRandom

import net.superkassa.models.Carrier
import net.superkassa.models.enums.Gds
import org.joda.time.DateTime
import net.superkassa.models.enums._
import net.superkassa.models.primitives.Partner
import net.superkassa.models.ranges.DateTimeRange
import org.joda.money.{CurrencyUnit, Money}

package object optimizer {

  sealed class OptimizerEventType(val code: String) extends Enum

  object OptimizerEventType {
    case object Landing extends OptimizerEventType("LANDING")
    case object Booking extends OptimizerEventType("BOOKING")
    case object Sale extends OptimizerEventType("SALE")
  }

  case class OptimizerEvent(
    dateTime: DateTime,
    eventType: OptimizerEventType,
    maybeGds: Option[Gds],
    maybeCarrier: Option[Carrier],
    maybePartner: Option[Partner],
    segmentsAmount: Int,
    price: Money,
    profit: Money,
  ) {
    
  }

  object OptimizerEvent {
    def random(n: Int): Seq[OptimizerEvent] = {
      val random = ThreadLocalRandom.current()
      def rand[T](values: Seq[T]): T = values(random.nextInt(values.size))
      val carriers = Seq(Carrier("SU"), Carrier("U6"), Carrier("S7")).map(Option(_)) ++ None
      val gds = Seq(Gds.Sirena, Gds.Amadeus, Gds.Travelport).map(Option(_)) ++ None
      val events = Seq.fill(10)(OptimizerEventType.Landing) ++ Seq.fill(2)(OptimizerEventType.Booking) ++ Seq.fill(1)(OptimizerEventType.Sale)
      val currencyUnit = CurrencyUnit.of("RUB")
      def randMoney(int: Int) = Money.of(currencyUnit, random.nextInt(int))
      def get: List[OptimizerEvent] = {
        val landing = OptimizerEvent(new DateTime().minusDays(random.nextInt(100)), OptimizerEventType.Landing, rand(gds), rand(carriers), None, 100, randMoney(10000), randMoney(1000))
        val r = random.nextInt(10)
        if (r >= 6) {
          val booking = landing.copy(eventType = OptimizerEventType.Booking)
          if (r >= 8) {
            val sale = booking.copy(eventType = OptimizerEventType.Sale)
            return landing :: booking :: sale :: Nil
          }
          return landing :: booking :: Nil
        }
        landing :: Nil
      }
      1 to n flatMap(_ => get)
    }
  }

  // event
  trait Target {
    val dateTimeRange: DateTimeRange
    val maybeGds: Option[Gds]
    val maybeCarrier: Option[Carrier]
    val maybePartner: Option[Partner]
  }

  case class SegmentsAmountTarget(

  )

}
