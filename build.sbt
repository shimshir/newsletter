name := "newsletter"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.6",
  "io.spray" %%  "spray-json" % "1.3.3",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.6" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test
)
