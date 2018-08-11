import sbt._

object Dependencies {

  object Versions {
    val scala212 = "2.12.6"
    val scalatest = "3.0.5"
    val protocJar = "3.6.0"
    val searchMavenOrgScalaSdk = "1.0.0"
  }

  object Compiles {
    val protocJar = "com.github.os72" % "protoc-jar" % Versions.protocJar
    val searchMavenSdk = "org.jmotor.tools" %% "search-maven-org-scala-sdk" % Versions.searchMavenOrgScalaSdk
  }

  object Tests {
    val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % Versions.scalatest % Test
  }

  import Compiles._

  lazy val dependencies: Seq[ModuleID] = Seq(searchMavenSdk, protocJar, Tests.scalaTest)

}
