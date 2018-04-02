package net.superkassa.models

package object primitives {

  case class BookingClass(code: String) extends Primitive

  case class CabinClass(code: String) extends Primitive

  case class CabinSubclass(code: String) extends Primitive

  case class FareType(code: String) extends Primitive

  case class FareBase(code: String) extends Primitive

  case class FareBrand(code: String) extends Primitive

  case class TourCode(code: String) extends Primitive

  case class SourceDocument(code: String, number: Int) extends Primitive

  case class Partner(code: String) extends Primitive

}
