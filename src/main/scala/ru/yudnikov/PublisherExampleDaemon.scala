package ru.yudnikov

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import ru.yudnikov.actors.Publisher
import ru.yudnikov.database.postgres.{PostgresSession, PostgresSettings}

object PublisherExampleDaemon extends App {
  val config = ConfigFactory.load("publisher.conf")
  val backendSession = PostgresSession(PostgresSettings(config.getConfig("superkassa.db.backend")))
  val frontendSession = PostgresSession(PostgresSettings(config.getConfig("superkassa.db.frontend")))
  val actorSystem = ActorSystem("superkassa", config)
  actorSystem.actorOf(Props(classOf[Publisher]))
}
