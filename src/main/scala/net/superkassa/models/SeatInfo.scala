package net.superkassa.models

case class SeatInfo(
  isSeated: Boolean
) {

  val isInfant: Boolean = !isSeated

}
