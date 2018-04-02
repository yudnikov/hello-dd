package net.superkassa.models

import net.superkassa.models.enums.TravellerType

case class SegmentRecommendation(
  fares: Set[Fare],
  validatingCarriers: Set[Carrier],
  categories: Map[TravellerType, String] = Map(),
  maybeRefundableMode: Option[String] = None,
  maybeChangeableMode: Option[String] = None,
  maybeBaggage: Option[Baggage] = None
)