package net.superkassa.models.enums

sealed abstract class TravellerType(val code: String, isSeated: Boolean) extends Enum

object TravellerType {
  case object Adult extends TravellerType("ADULT", true)
  case object Child extends TravellerType("CHILD", true)
  case object Infant extends TravellerType("INFANT", false)
}