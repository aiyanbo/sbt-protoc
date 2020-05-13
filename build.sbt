import Dependencies._
import ReleaseTransformations._

lazy val utf8: String = "UTF-8"
lazy val javaVersion: String = "1.8"

sbtPlugin := true

name := "sbt-protoc"

organization := "org.jmotor.sbt"

libraryDependencies ++= dependencies

enablePlugins(ScriptedPlugin)

Compile / compile / javacOptions ++= Seq(
  "-source", javaVersion, "-target", javaVersion, "-encoding", utf8, "-deprecation"
)

Compile / doc / javacOptions ++= Seq(
  "-linksource", "-source", javaVersion, "-docencoding", utf8, "-charset", utf8, "-encoding", utf8, "-nodeprecated"
)

scriptedBufferLog := false

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
}

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)
