package net.superkassa.models.primitives

trait Primitive extends java.io.Serializable {
  val code: String
  override def equals(obj: scala.Any): Boolean = {
    obj match {
      case p: Primitive if code == p.code && getClass == p.getClass => true
      case _ => false
    }
  }
  override def hashCode(): Int = 41 * code.hashCode
}