package net.superkassa.calculator

import net.superkassa.calculator.blacklist.BlacklistRule
import net.superkassa.models.geography.{City, Port}

case class DataImage(
  portsToCities: Map[Port, City],
  blacklistRules: Set[BlacklistRule]
) {

}
