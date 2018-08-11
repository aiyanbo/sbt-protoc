import Dependencies._

sbtPlugin := true

name := "sbt-protoc"

version := "1.0.0-SNAPSHOT"

organization := "org.jmotor.sbt"

libraryDependencies ++= dependencies

dependencyUpgradeModuleNames := Map(
  "coursier-.*" -> "coursier"
)

enablePlugins(ScriptedPlugin)

scriptedBufferLog := false

scriptedLaunchOpts := {
  scriptedLaunchOpts.value ++
    Seq("-Xmx1024M", s"-Dplugin.version=${version.value}")
}
