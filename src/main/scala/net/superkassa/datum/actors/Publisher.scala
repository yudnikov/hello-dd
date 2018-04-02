package net.superkassa.datum.actors

import akka.actor.{Actor, Props}
import akka.cluster.Cluster
import akka.cluster.ddata._
import akka.cluster.ddata.Replicator.{Update, WriteAll}
import net.superkassa.calculator.CalculatorContext
import net.superkassa.datum.{DatabaseReader, DatumContext}
import net.superkassa.datum.actors.Publisher.Refresh
import org.json4s.Formats
import ru.yudnikov.database.postgres.PostgresSession
import ru.yudnikov.utils.{MapDiff, SetDiff}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class Publisher(backendSession: PostgresSession, frontendSession: PostgresSession, datumContext: DatumContext) extends Actor with DatabaseReader {

  implicit val cluster: Cluster = Cluster(context.system)

  private val replicator = DistributedData(context.system).replicator
  private val refreshInterval = 5.seconds
  private val writeAllTimeout = 5.seconds

  def updateMap[K, V](keyString: String, value: Map[K, V]): Unit = {
    println(s"updating map $keyString\n$value")
    val key = LWWMapKey[K, V](keyString)
    replicator ! Update(key, LWWMap.empty[K, V], WriteAll(writeAllTimeout)) { current =>
      val diff = MapDiff(current.entries, value)
      val updated = diff.updated.foldLeft(current)(_ + _)
      diff.removed.foldLeft(updated)(_ - _)
    }
  }

  def updateSet[A](keyString: String, value: Set[A]): Unit = {
    println(s"updating set $keyString\n$value")
    val key = ORSetKey[A](keyString)
    replicator ! Update(key, ORSet.empty[A], WriteAll(writeAllTimeout)) { current =>
      val diff = SetDiff(current.elements, value)
      val added = diff.added.foldLeft(current)(_ + _)
      diff.removed.foldLeft(added)(_ - _)
    }
  }

  context.system.scheduler.schedule(0.second, refreshInterval, self, Refresh)

  override def receive: Receive = {
    case Refresh =>
      val futures = List(
        portsToCitiesFuture.map(updateMap("portsToCities", _)),
        blacklistRulesFuture.map(updateSet("blacklistRules", _)),
        segmentBonusRulesFuture(datumContext).map(updateSet("segmentBonusRules", _)),
        thresholdsFuture(datumContext).map(updateSet("thresholds", _)),
        interlinesFuture.map(updateMap("interlines", _)),
        clearingRulesFuture.map(updateSet("clearingRules", _))
      )
      val resultFuture = Future.sequence(futures)
      resultFuture.onComplete {
        case Failure(exception) =>
          throw exception
        case _ =>
          println(s"published data!")
      }
  }
}

object Publisher {

  object Refresh

}
