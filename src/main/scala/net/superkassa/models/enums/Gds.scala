package net.superkassa.models.enums

import net.superkassa.annotations.Synonym
import net.superkassa.models.Stock
import org.joda.time.Hours

sealed abstract class Gds(val code: String) extends Enum {
  def stocks: Iterable[Stock]
  def minDepartureTime: Hours = Hours.ZERO
  @Synonym("regular", "irregular", "isRegular")
  val isCharter: Boolean = false
  val maybeClearingCompany: Option[ClearingCompany]
}

object Gds {

  object Amadeus extends Gds("AMADEUS") {
    override def stocks: List[Stock] = Stock(Market.Ru, Shop.Default, ClearingCompany.Bsp) :: Nil
    override def minDepartureTime: Hours = Hours.hours(6)
    override val maybeClearingCompany: Option[ClearingCompany] = Some(ClearingCompany.Bsp)
  }

  object Sirena extends Gds("SIRENA") {
    override def stocks: Iterable[Stock] =
      List(Stock(Market.Ru, Shop.Default, ClearingCompany.Tch), Stock(Market.Ru, Shop.Direct, ClearingCompany.Tch))
    override val maybeClearingCompany: Option[ClearingCompany] = Some(ClearingCompany.Tch)
  }

  object Travelport extends Gds("TRAVELPORT") {
    override def stocks: List[Stock] =
      List(Stock(Market.Ru, Shop.Default, ClearingCompany.Bsp), Stock(Market.Ru, Shop.Default, ClearingCompany.Tch))
    override def minDepartureTime: Hours = Hours.hours(3)
    override val maybeClearingCompany: Option[ClearingCompany] = None
  }

  object Charter extends Gds("CHARTER") {
    override def stocks: List[Stock] = Nil
    override val isCharter: Boolean = true
    override val maybeClearingCompany: Option[ClearingCompany] = None
  }

  object ProxyGds extends Gds("PROXY_GDS") {
    override def stocks: Iterable[Stock] = Nil
    override def minDepartureTime: Hours = Hours.hours(24)
    override val maybeClearingCompany: Option[ClearingCompany] = None
  }

  def unapply(string: String): Option[Gds] = {
    string.toUpperCase match {
      case "AMADEUS" | "X" => Some(Amadeus)
      case "SIRENA" | "Y" => Some(Sirena)
      case "TRAVELPORT" | "Z" => Some(Travelport)
      case "CHARTER" | "V" => Some(Charter)
      case "PROXYGDS" | "W" => Some(ProxyGds)
      case _ => None
    }
  }

}
