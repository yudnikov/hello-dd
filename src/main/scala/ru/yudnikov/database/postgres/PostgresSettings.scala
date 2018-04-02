package ru.yudnikov.database.postgres

import com.typesafe.config.Config

case class PostgresSettings(host: String, port: Int, database: String, login: String, password: String)

object PostgresSettings {
  def apply(config: Config): PostgresSettings = {
    val host = config.getString("host")
    val port = config.getInt("port")
    val database = config.getString("database")
    val login = config.getString("login")
    val password = config.getString("password")
    PostgresSettings(host, port, database, login, password)
  }
}
