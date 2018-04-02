name := "superkassa"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  // joda
  "joda-time" % "joda-time" % "2.9.9",
  "org.joda" % "joda-money" % "0.12",
  // config
  "com.typesafe" % "config" % "1.2.1",
  // test
  "org.scalatest" % "scalatest_2.11" % "2.1.3" % Test,
  // postgres
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  // akka
  "com.typesafe.akka" %% "akka-actor" % "2.5.4",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.4",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.4",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.4",
  // akka persistence
  "com.typesafe.akka" %% "akka-persistence" % "2.5.4",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  // json4s
  "org.json4s" %% "json4s-jackson" % "3.5.2"
)