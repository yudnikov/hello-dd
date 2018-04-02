package net.superkassa.models.enums

import net.superkassa.models.primitives.Primitive

trait Enum extends Primitive {
  override def toString: String = code
}
