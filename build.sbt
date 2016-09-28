lazy val versions = new {
  val scala = "2.11.8"
  val finch = "0.11.0-M3"
}

lazy val commonSettings = Seq (
  version := "0.1.3-SNAPSHOT",
  organization := "ru.arkoit",
  scalaVersion := versions.scala,
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
  ),
  crossScalaVersions := Seq("2.11.8", "2.10.6"),
  scalacOptions ++= Seq("-feature", "-language:implicitConversions"),
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ => false },
  licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  homepage := Some(url("https://github.com/akozhemiakin/finchrich")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/akozhemiakin/finchrich"),
      "scm:git:git@github.com:akozhemiakin/finchrich.git"
    )
  ),
  autoAPIMappings := true,
  pomExtra := (
    <developers>
      <developer>
        <id>akozhemiakin</id>
        <name>Artyom Kozhemiakin</name>
        <url>http://arkoit.ru</url>
      </developer>
    </developers>)
)

lazy val allSettings = commonSettings ++ publishSettings

lazy val finchrich = (project in file("."))
  .settings(allSettings)
  .settings(noPublish)
  .aggregate(controller)

lazy val controller = project
  .settings(allSettings)
  .settings(Seq(
    moduleName := "finchrich-controller",
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "com.github.finagle" %% "finch-core" % versions.finch
    )
  ))
