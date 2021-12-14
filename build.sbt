name := "backend_code_challenge_EduardSG"

organization := "com.eduardsimon"

version := "0.1"

scalaVersion := "2.13.5"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

parallelExecution in Test := false

libraryDependencies ++= {
  val akkaHttpV = "10.2.4"
  val akkaV = "2.6.14"
  val scalaTestV = "3.2.8"
  val sprayV = "1.3.6"
  val catsV = "2.0.0"
  val scalamockV ="5.1.0"

  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "io.spray" %% "spray-json" % sprayV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test",
    "org.scalatest" %% "scalatest" % scalaTestV % "test",
    "org.typelevel" %% "cats-core" % catsV,
    "org.scalamock" %% "scalamock" % scalamockV % "test"

  )
}

Revolver.settings