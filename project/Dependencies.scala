import sbt._

object Dependencies {

  object Versions {
    val protocJar = "3.8.0"
    val scalatest = "3.0.8"
    val scala212 = "2.12.8"
    val sbtLaunch = "1.3.2"
    val scriptedSbt = "1.3.2"
    val scalaLibrary = "2.13.1"
    val artifactVersions = "1.0.6"
  }

  object Compiles {
    val protocJar = "com.github.os72" % "protoc-jar" % Versions.protocJar
    val artifactVersions = "org.jmotor.artifact" %% "artifact-versions" % Versions.artifactVersions
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(artifactVersions, protocJar, Tests.scalaTest)

}
