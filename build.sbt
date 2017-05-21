name := "newsletter"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.6",
  "org.scalatest" %% "scalatest" % "3.0.3" % Test
)
