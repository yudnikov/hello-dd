package net.superkassa.datum

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import net.superkassa.calculator.CalculatorContext
import net.superkassa.datum.actors.Publisher
import ru.yudnikov.database.postgres.{PostgresSession, PostgresSettings}

import scala.concurrent.Await
import scala.concurrent.duration._

object DatumDaemon extends App {
  val config = ConfigFactory.load("publisher.conf")
  val backendSession = Await.result(PostgresSession(PostgresSettings(config.getConfig("superkassa.db.backend"))), 5.seconds)
  val frontendSession = Await.result(PostgresSession(PostgresSettings(config.getConfig("superkassa.db.frontend"))), 5.seconds)
  val actorSystem = ActorSystem("superkassa", config)
  val calculatorContext = DatumContext(config)
  actorSystem.actorOf(Props(classOf[Publisher], backendSession, frontendSession, calculatorContext))
}
