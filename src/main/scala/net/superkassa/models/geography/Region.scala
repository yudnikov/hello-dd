package net.superkassa.models.geography

case class Region(code: String) extends Location {
  override def contains(location: Location)(implicit geography: Geography): Boolean = ???
}