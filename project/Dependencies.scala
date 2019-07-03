import sbt._

object Dependencies {

  object Versions {
    val scalatest = "3.0.6"
    val scala212 = "2.12.8"
    val protocJar = "3.7.0"
    val scalaLibrary = "2.12.8"
    val artifactVersions = "1.0.5"
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
