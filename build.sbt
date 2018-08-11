import Dependencies._
import ReleaseTransformations._

sbtPlugin := true

name := "sbt-protoc"

organization := "org.jmotor.sbt"

libraryDependencies ++= dependencies

enablePlugins(ScriptedPlugin)

scriptedBufferLog := false

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
}

releasePublishArtifactsAction := PgpKeys.publishLocalSigned.value

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
