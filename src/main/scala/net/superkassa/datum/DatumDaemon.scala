package net.superkassa.datum

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import net.superkassa.calculator.CalculatorContext
import net.superkassa.datum.actors.Publisher
import ru.yudnikov.database.postgres.{PostgresSession, PostgresSettings}

object DatumDaemon extends App {
  val config = ConfigFactory.load("publisher.conf")
  val backendSession = PostgresSession(PostgresSettings(config.getConfig("superkassa.db.backend")))
  val frontendSession = PostgresSession(PostgresSettings(config.getConfig("superkassa.db.frontend")))
  val actorSystem = ActorSystem("superkassa", config)
  val calculatorContext = DatumContext(config)
  actorSystem.actorOf(Props(classOf[Publisher], backendSession, frontendSession, calculatorContext))
}
