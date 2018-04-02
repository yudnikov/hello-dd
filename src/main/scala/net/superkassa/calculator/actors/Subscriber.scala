package net.superkassa.calculator.actors

import akka.actor.Actor
import akka.cluster.ddata.{DistributedData, LWWMapKey, ORSetKey}
import akka.cluster.ddata.Replicator._
import akka.pattern.ask
import akka.util.Timeout
import net.superkassa.calculator.DataImage
import net.superkassa.calculator.blacklist.BlacklistRule
import net.superkassa.calculator.rules.SegmentBonusRule
import net.superkassa.models.geography.{City, Port}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class Subscriber() extends Actor {
  private val replicator = DistributedData(context.system).replicator
  replicator ! Subscribe(LWWMapKey("portsToCities"), self)
  replicator ! Subscribe(ORSetKey("blacklistRules"), self)
  replicator ! Subscribe(ORSetKey("segmentBonusRules"), self)
  replicator ! Subscribe(ORSetKey("thresholds"), self)
  replicator ! Subscribe(ORSetKey("interlines"), self)

  override def receive: Receive = {
    case changed@Changed(key) =>
      val value = changed.get(key)
      println(s"changed: $key\n$value")
    case get@GetSuccess(key, _) =>
      println(s"got ${get.get(key)}")
    case get@GetFailure(key, _) =>
      println(s"failure ${get}")
    case NotFound(key, _) =>
      println(s"key not found $key")
  }
}
