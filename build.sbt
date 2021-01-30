name := "generic-mongo-repo"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.1.1"

libraryDependencies += "com.github.javafaker" % "javafaker" % "1.0.2"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
