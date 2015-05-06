resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-remote" % "2.3.10",
  "com.typesafe.akka" %% "akka-actor" % "2.3.10"
)