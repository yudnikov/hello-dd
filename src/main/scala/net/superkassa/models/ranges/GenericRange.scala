package net.superkassa.models.ranges

trait GenericRange[T] {
  val from: Option[T]
  val to: Option[T]
  def toOrdered(value: T): Ordered[T]
  def contains(value: T): Boolean = {
    from -> to match {
      case (Some(f), Some(t)) =>
        toOrdered(f) >= value && toOrdered(t) <= t
      case (Some(f), None) =>
        toOrdered(f) <= value
      case (None, Some(t)) =>
        toOrdered(t) >= value
      case _ =>
        true
    }
  }
  def contains(dateTimeRange: DateTimeRange): Boolean = ???
}
