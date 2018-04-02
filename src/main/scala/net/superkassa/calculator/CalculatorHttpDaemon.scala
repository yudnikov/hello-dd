package net.superkassa.calculator

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import net.superkassa.calculator.actors.Subscriber

object CalculatorHttpDaemon extends App {
  val config = ConfigFactory.load("subscriber.conf")
  val actorSystem = ActorSystem("superkassa", config)
  actorSystem.actorOf(Props(classOf[Subscriber]))

}
