import Dependencies.Library
import Dependencies.Library
import sbt.Resolver.bintrayRepo

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := Dependencies.Version.scala

// Convenient tool for cleaning embedded data
val cleanCas = taskKey[Unit]("Delete embedded-cassandra data.")
val fixKafka = taskKey[Unit]("When kafka won't start, run this to delete zookeeper data.")

lazy val root = (project in file("."))
  .settings(name := "AbsTTraction")
  .aggregate(reproducer)
  .settings(
    fixKafka := IO.delete(baseDirectory.value / "target/lagom-dynamic-projects/lagom-internal-meta-project-kafka/target/zookeeper_data"),
    cleanCas := IO.delete(baseDirectory.value / "target/embedded-cassandra/data")
  )

lazy val reproducer = (project in file("reproducer"))
  .enablePlugins(PlayScala && LagomPlay)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslServer,
      ws
    ) ++ Library.MacWire.corePack,
  )
