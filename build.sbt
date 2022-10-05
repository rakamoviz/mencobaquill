import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

libraryDependencies ++= Seq(
  "io.getquill"          %% "quill-jdbc" % "3.4.10",
  "org.postgresql"       % "postgresql" % "42.2.8",
  "com.chuusai" %% "shapeless" % "2.3.3"
)

lazy val root = (project in file("."))
  .settings(
    name := "mencobaquill",
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
