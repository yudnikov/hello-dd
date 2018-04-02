package ru.yudnikov

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import ru.yudnikov.actors.Subscriber

object SubscriberExampleDaemon extends App {
  val config = ConfigFactory.load("subscriber.conf")
  val actorSystem = ActorSystem("superkassa", config)
  actorSystem.actorOf(Props(classOf[Subscriber]))
}
