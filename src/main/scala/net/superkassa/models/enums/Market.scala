package net.superkassa.models.enums

sealed class Market(val code: String) extends Enum

object Market {

  case object Ru extends Market("RU")
  case object De extends Market("DE")

  def apply(string: String): Market = {
    string.toUpperCase match {
      case Market(code) => new Market(code)
      case _ => throw new Exception(s"can't get market from string $string")
    }
  }

  def unapply(string: String): Option[String] = {
    string.toUpperCase match {
      case str@("RU" | "DE") => Some(str)
      case _ => None
    }
  }
}