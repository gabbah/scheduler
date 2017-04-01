name := "scheduler"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.13.1",
  "com.github.finagle" %% "finch-circe" % "0.13.1",
  "io.circe" %% "circe-generic" % "0.7.0",
  "org.json4s" %% "json4s-jackson" % "3.5.1"

)

mainClass in Global := Some("app.Main")
