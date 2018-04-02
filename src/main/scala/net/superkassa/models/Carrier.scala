package net.superkassa.models

import net.superkassa.models.enums.Gds

case class Carrier(code: String)

object Carrier {

  def perStock(gds: Gds, carriers: Set[Carrier]): Map[Stock, Set[Carrier]] = {
    ???
  }

}
