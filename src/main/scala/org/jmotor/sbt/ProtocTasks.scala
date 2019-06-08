package org.jmotor.sbt

import java.nio.charset.StandardCharsets
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.{ Files, Path, Paths }
import java.util.Comparator
import java.util.stream.Collectors

import com.github.os72.protocjar.Protoc
import org.jmotor.tools.MavenSearchClient
import sbt.io.Using
import sbt.{ IO, url }

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{ Await, Future }
import scala.util.Properties

/**
 * Component:
 * Description:
 * Date: 2018/8/11
 *
 * @author AI
 */
object ProtocTasks {

  private[sbt] val timeout = 5.minutes
  private[sbt] lazy val client = MavenSearchClient()
  private[sbt] val protocArtifactId = "protoc-gen-grpc-java"

  def getGrpcLatestVersion: String = {
    Await.result(resolveVersion("io.grpc", protocArtifactId), timeout)
  }

  def getProtocLatestVersion: String = {
    Await.result(resolveVersion("com.google.protobuf", "protobuf-java"), timeout)
  }

  def compileProto(protocVersion: String, grpcVersion: String, sourceDirectory: Path,
                   javaOutDirectory: Path, includeStdTypes: Boolean = false): Seq[Path] = {
    if (Files.exists(javaOutDirectory)) {
      val stream = Files.walk(javaOutDirectory)
      val paths = try {
        stream.sorted(Comparator.reverseOrder[Path]()).collect(Collectors.toList[Path])
      } finally {
        stream.close()
      }
      paths.remove(paths.size() - 1)
      paths.asScala.foreach(Files.delete)
    } else {
      Files.createDirectories(javaOutDirectory)
    }
    val plugin = resolveGrpcPlugin(protocArtifactId, grpcVersion)
    val sources = getProtoSources(sourceDirectory)
    val options = if (includeStdTypes) {
      Array("--include_std_types")
    } else {
      Array.empty[String]
    }
    if (sources.nonEmpty) {
      Protoc.runProtoc(options ++
        Array(
          s"-v$protocVersion",
          s"--java_out=$javaOutDirectory", s"-I=$sourceDirectory") ++ sources.map(_.toString))
      val grpcSources = getGrpcSources(sources)
      if (grpcSources.nonEmpty) {
        Protoc.runProtoc(options ++
          Array(
            s"-v$protocVersion",
            s"--plugin=$protocArtifactId=$plugin",
            s"--grpc-java_out=$javaOutDirectory",
            s"--proto_path=$sourceDirectory") ++ grpcSources.map(_.toString))
      }
      val stream = Files.walk(javaOutDirectory)
      try {
        stream.filter(p ⇒ Files.isRegularFile(p)).collect(Collectors.toList[Path]).asScala
      } finally {
        stream.close()
      }
    } else {
      sources
    }
  }

  def hasGrpcSource(sourceDirectory: Path): Boolean = {
    val sources = getProtoSources(sourceDirectory)
    getGrpcSources(sources).nonEmpty
  }

  def getGrpcSources(sources: Seq[Path]): Seq[Path] = {
    sources.filter(p ⇒ Files.readAllLines(p, StandardCharsets.UTF_8).asScala.exists(_.startsWith("service")))
  }

  private[sbt] def getProtoSources(sourceDirectory: Path): Seq[Path] = {
    if (Files.exists(sourceDirectory)) {
      val visitor = new ProtoFileVisitor()
      Files.walkFileTree(sourceDirectory, visitor)
      visitor.result()
    } else {
      Seq.empty
    }
  }

  private[sbt] def resolveGrpcPlugin(artifactId: String, version: String): String = {
    val artifactName = getGrpcArtifactName(artifactId, version)
    val binaryHome = Paths.get(Properties.userHome, ".sbt", "protoc")
    val exe = binaryHome.resolve(artifactName)
    if (Files.notExists(exe)) {
      Files.createDirectories(binaryHome)
      Using.urlInputStream(url(s"https://repo1.maven.org/maven2/io/grpc/$artifactId/$version/$artifactName")) { is ⇒
        IO.transfer(is, exe.toFile)
      }
      Files.setPosixFilePermissions(exe, Set(PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ).asJava)
    }
    exe.toString
  }

  private[sbt] def resolveVersion(groupId: String, artifactId: String): Future[String] = {
    client.latestVersion(groupId, artifactId).map {
      case None    ⇒ throw new NullPointerException(s"cannot get $groupId:$artifactId latest version")
      case Some(v) ⇒ v
    }
  }

  private[sbt] def getGrpcArtifactName(artifactId: String, version: String): String = {
    val os = if (Properties.isMac) {
      "osx-x86_64"
    } else if (Properties.isWin) {
      "windows-x86_64"
    } else {
      "linux-x86_64"
    }
    s"$artifactId-$version-$os.exe"
  }

}
