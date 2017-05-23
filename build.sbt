name := "newsletter"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6",
  "io.spray" %%  "spray-json" % "1.3.3",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.github.nikita-volkov" % "sext" % "0.2.4",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test
)

enablePlugins(JavaAppPackaging)

coverageMinimum := 80
coverageFailOnMinimum := false
