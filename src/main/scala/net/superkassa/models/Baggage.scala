package net.superkassa.models

case class Baggage(
  baggageCode: String,
  freeAllowance: Int,
  maybeUnitQualifier: Option[String]
)