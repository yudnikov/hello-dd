package net.superkassa.models.enums

sealed abstract class ClearingCompany(val code: String) extends Enum

object ClearingCompany {
  case object Tch extends ClearingCompany("TCH")
  case object Bsp extends ClearingCompany("BSP")
}