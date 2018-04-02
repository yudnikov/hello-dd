package ru.yudnikov.actors

import akka.actor.Actor
import akka.cluster.ddata.{DistributedData, ORSetKey}
import akka.cluster.ddata.Replicator.{Changed, Subscribe}

class Subscriber extends Actor {
  val replicator = DistributedData(context.system).replicator
  val key = ORSetKey[String]("key")
  replicator ! Subscribe(key, self)
  override def receive: Receive = {
    case x@Changed(ORSetKey("key")) =>
      val data = x.get(key)
      println(s"got ${data}")
    case x =>
      println(s"received $x")
  }
}
