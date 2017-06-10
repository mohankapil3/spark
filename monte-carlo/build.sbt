name := "my-app"

version := "0.1"

scalaVersion := "2.10.6"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"
libraryDependencies += "org.apache.spark" %% "spark-core" % "1.6.2" % "provided"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.6.2" % "provided"

