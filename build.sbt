name := """rate-my-area"""
organization := "io.stuartp"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  evolutions,
  jdbc,
  ehcache,
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.0",
  "com.softwaremill.macwire" %% "proxy" % "2.3.0",
  "com.typesafe.play" %% "play-slick" % "3.0.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1",
  "org.postgresql" % "postgresql" % "9.4.1207.jre7",
  "de.svenkubiak" % "jBCrypt" % "0.4.1",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "io.stuartp.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "io.stuartp.binders._"
