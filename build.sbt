lazy val versions = new {
  val scala = "2.11.8"
  val finch = "0.10.0"
}

lazy val commonSettings = Seq (
  version := "0.1.0-SNAPSHOT",
  organization := "ru.arkoit",
  scalaVersion := versions.scala,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
  ),
  crossScalaVersions := Seq("2.11.8", "2.10.6"),
  scalacOptions ++= Seq("-feature", "-language:implicitConversions"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val finchrich = (project in file("."))
  .settings(commonSettings)
  .aggregate(core)

lazy val core = project
  .settings(commonSettings)
  .settings(Seq(
    moduleName := "core",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.github.finagle" %% "finch-core" % versions.finch
    )
  ))
