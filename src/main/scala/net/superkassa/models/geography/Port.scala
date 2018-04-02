package net.superkassa.models.geography

case class Port(code: String) extends Location {
  override def contains(location: Location)(implicit geography: Geography): Boolean = ???
  lazy val city: City = ???
  lazy val country: Country = ???
  lazy val region: Region = ???
}


