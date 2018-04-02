package net.superkassa.models

import net.superkassa.models.geography.Port
import org.joda.time.DateTime

case class TechStop(
  port: Port,
  arrival: DateTime,
  departure: DateTime
)