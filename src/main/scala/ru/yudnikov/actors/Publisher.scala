package ru.yudnikov.actors

import java.util.concurrent.ThreadLocalRandom

import akka.actor.Actor
import akka.cluster.Cluster
import akka.cluster.ddata.DurableStore.Store
import akka.cluster.ddata.Replicator._
import akka.cluster.ddata.{DistributedData, ORSet, ORSetKey}
import ru.yudnikov.actors.Publisher.{Init, Refresh}

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await

class Publisher extends Actor {
  type T = String
  implicit val cluster = Cluster(context.system)
  val replicator = DistributedData(context.system).replicator
  context.system.scheduler.schedule(5.second, 5.second, self, Refresh)
  val key = ORSetKey[String]("key")
  self ! Init
  var state: ORSet[String] = ORSet.empty[String]

  override def receive: Receive = {
    case Init =>
      val initData = "init"
      println(state)
      replicator ! Update(key, ORSet(), WriteAll(1.hour)) { x => x + initData }
    case Refresh =>
      val newData = rand
      implicit val timeout: Timeout = 1.hour
      //val updates = ORSet.empty[String] + newData + s"another_$newData"
      replicator ! Update(key, ORSet.empty[String], WriteAll(1.hour)) { x => x + newData}
    case x@NotFound(ORSetKey("key"), _) =>
      println(s"not found $x")
    case x@GetSuccess(ORSetKey("key"), _) =>
      val got = x.get(key)
      println(s"got $got with delta ${got.delta}")
    case x@UpdateSuccess(ORSetKey("key"), _) =>
      println(s"updated $x")
      //replicator ! Get(key, ReadLocal)
    case x@Changed(ORSetKey("key")) =>
      val got = x.get(key)
      got
    case x =>
      println(s"got $x")
      x
  }

  def rand: String = ThreadLocalRandom.current().nextInt(5).toString

}

object Publisher {

  object Refresh

  object Init

}
