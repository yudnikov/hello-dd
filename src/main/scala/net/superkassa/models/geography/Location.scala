package net.superkassa.models.geography

import net.superkassa.models.primitives.Primitive

trait Location extends Primitive {
  def contains(location: Location)(implicit geography: Geography): Boolean
}
