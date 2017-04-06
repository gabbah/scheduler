name := "scheduler"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.13.1",
  "com.github.finagle" %% "finch-circe" % "0.13.1",
  "io.circe" %% "circe-generic" % "0.7.0",
  "org.json4s" %% "json4s-jackson" % "3.5.1",
  "org.cassandraunit" % "cassandra-unit" % "3.1.3.2",
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.1.4",
//  "org.slf4j" % "slf4j-log4j12" % "1.7.25",
  "com.outworkers"  %% "phantom-dsl" % "2.6.1",
  "com.outworkers" %% "phantom-finagle" % "2.6.1"

)

mainClass in Global := Some("app.Main")
