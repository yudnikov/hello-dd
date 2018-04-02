package net.superkassa.models.geography

case class Country(code: String) extends Location {
  override def contains(location: Location)(implicit geography: Geography): Boolean = ???
  lazy val region: Region = ???
}
