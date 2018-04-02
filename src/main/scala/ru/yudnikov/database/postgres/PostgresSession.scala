package ru.yudnikov.database.postgres

import java.sql._
import org.postgresql.PGConnection
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class PostgresSession(connection: Connection with PGConnection) {

  def execute(query: String): Unit = {
    val statement = connection.createStatement()
    println(s"executing:\n$query")
    statement.execute(query)
  }

  def executeQuery[T](query: String)(transform: ResultSet => T): Future[Iterator[T]] = Future {
    val statement = connection.createStatement()
    val rs = statement.executeQuery(query)
    new Iterator[T] {
      override def hasNext: Boolean = rs.next()
      override def next(): T = transform(rs)
    }
  }
}

object PostgresSession {
  def apply(postgresSettings: PostgresSettings): PostgresSession = {
    import postgresSettings._
    DriverManager.registerDriver(new org.postgresql.Driver)
    val connection = DriverManager.getConnection(s"jdbc:postgresql://$host:$port/$database", login, password).asInstanceOf[Connection with PGConnection]
    PostgresSession(connection)
  }
}
