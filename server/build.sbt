lazy val root = (project in file(".")).
settings(
name := "server",
version := "1.0",
scalaVersion := "2.11.5",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-remote" % "2.3.10",
      "com.typesafe.akka" %% "akka-actor" % "2.3.10",
      "io.spray" %% "spray-can" % "1.3.3",
      "io.spray" %% "spray-http" % "1.3.3",
      "io.spray" %% "spray-httpx" % "1.3.3",
      "io.spray" %% "spray-util" % "1.3.3",
      "io.spray" %% "spray-client" % "1.3.3"
    ),

    resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/","spray repo" at "http://repo.spray.io")
)




