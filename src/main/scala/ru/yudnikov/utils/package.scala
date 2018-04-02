package ru.yudnikov

import java.util.Date

import org.joda.money.CurrencyUnit
import org.joda.time.DateTime

package object utils {
  case class MapDiff[K, V](updated: Map[K, V], removed: Set[K])
  object MapDiff {
    def apply[K, V](a: Map[K, V], b: Map[K, V]): MapDiff[K, V] = {
      val updated = (b.toSet diff a.toSet).toMap
      val removed = a.keys.toSet diff b.keys.toSet
      MapDiff(updated, removed)
    }
  }
  case class SetDiff[A](added: Set[A], removed: Set[A])
  object SetDiff {
    def apply[A](a: Set[A], b: Set[A]): SetDiff[A] = {
      val added = b diff a
      val removed = a diff b
      new SetDiff(added, removed)
    }
  }
  implicit class StringExt(underlying: String) {
    lazy val toCurrency: CurrencyUnit = CurrencyUnit.of(underlying)
  }
  implicit class DateExt(value: Date) {
    lazy val asJoda: DateTime = new DateTime(value)
  }
}
