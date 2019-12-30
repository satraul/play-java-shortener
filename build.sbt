name := """play-java-shortener"""
organization := "com.satraul"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  javaJpa,
  "org.postgresql" % "postgresql" % "42.2.9",
  "org.hibernate" % "hibernate-core" % "5.4.9.Final",
  "org.hashids" % "hashids" % "1.0.1"
)

