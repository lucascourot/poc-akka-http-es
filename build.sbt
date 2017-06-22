scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.8" % Test
)
