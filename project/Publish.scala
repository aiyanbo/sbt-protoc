import sbt.Keys._
import sbt.{ AutoPlugin, Credentials, Path, PluginTrigger, _ }

object Publish extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[_root_.sbt.Def.Setting[_]] = Seq(
    publishMavenStyle := true,
    pomAllRepositories := false,
    pomIncludeRepository := { repo: MavenRepository ⇒
      lazy val port = new URL(repo.root).getPort
      !repo.root.startsWith("file:") && Seq(-1, 80, 443).contains(port)
    },
    credentials += Credentials(Path.userHome / ".sbt" / ".credentials"),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value) {
        Some("snapshots" at nexus + "content/repositories/snapshots")
      } else {
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
      }
    },
    pomExtra :=
      <url>https://github.com/aiyanbo/sbt-protoc</url>
      <licenses>
        <license>
          <name>Apache License</name>
          <url>https://www.apache.org/licenses/</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:aiyanbo/sbt-protoc.git</url>
        <connection>scm:git:git@github.com:aiyanbo/sbt-protoc.git</connection>
      </scm>
      <developers>
        <developer>
          <id>yanbo.ai</id>
          <name>Andy Ai</name>
          <url>https://aiyanbo.github.io/</url>
        </developer>
      </developers>)
}
