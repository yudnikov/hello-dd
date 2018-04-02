package net.superkassa.models.enums

sealed abstract class PaymentType(val code: String) extends Enum

object PaymentType {
  case object CREDIT_CARD extends PaymentType("CC")
}
