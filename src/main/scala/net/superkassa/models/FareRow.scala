package net.superkassa.models

import net.superkassa.models.primitives._

case class FareRow(
  fareBase: FareBase,
  maybeFareBrand: Option[FareBrand],
  multiSegment: Boolean,
  maybeTourCode: Option[TourCode] = None
)