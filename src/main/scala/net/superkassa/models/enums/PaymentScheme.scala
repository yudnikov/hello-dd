package net.superkassa.models.enums

import net.superkassa.exceptions.ApplyEnumFromStringException

sealed abstract class PaymentScheme(val code: String) extends Enum

object PaymentScheme {

  case object CreditCard extends PaymentScheme("CC")
  case object Cash extends PaymentScheme("CASH")
  case object Combo  extends PaymentScheme("CC+CASH")

  def apply(string: String): PaymentScheme = {
    string match {
      case "CC" => CreditCard
      case "CASH" => Cash
      case "CC+CASH" | "CASH+CC" => Combo
      case str =>
        throw ApplyEnumFromStringException(str, classOf[PaymentScheme])
    }
  }

}