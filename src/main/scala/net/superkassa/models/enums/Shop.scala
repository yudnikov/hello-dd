package net.superkassa.models.enums

import net.superkassa.exceptions.ApplyEnumFromStringException

sealed abstract class Shop(val code: String) extends Enum

object Shop {

  case object Default extends Shop("DEFAULT")
  case object Direct extends Shop("DIRECT")

  def apply(string: String): Shop = string match {
    case Shop(shop) => shop
    case str => throw ApplyEnumFromStringException(str, classOf[Shop])
  }

  def unapply(string: String): Option[Shop] = {
    string.toUpperCase match {
      case "DEFAULT" => Some(Default)
      case "DIRECT" => Some(Direct)
      case _ => None
    }
  }

}