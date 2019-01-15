package org.jmotor.sbt

import sbt.Keys._
import sbt._
import sbt.internal.io.Source
import sbt.plugins.JvmPlugin

/**
 * Component:
 * Description:
 * Date: 2018/8/9
 *
 * @author AI
 */
object ProtocPlugin extends ScopedProtocPlugin(Compile) {

  val autoImport = ProtocKeys

}

object ProtocTestPlugin extends ScopedProtocPlugin(Test, "test")

class ScopedProtocPlugin(configuration: Configuration, postfix: String = "main") extends AutoPlugin {

  object ProtocKeys extends ProtocKeys {

    lazy val ProtocConfig: Configuration = Configuration.of("ProtocConfig", s"protoc-$postfix")

    val watchSourcesSetting = watchSources += new Source((sourceDirectory in ProtocConfig).value, "*.proto", AllPassFilter)
  }

  import ProtocKeys._

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements

  override def projectConfigurations: Seq[Configuration] = Seq(ProtocConfig)

  override def projectSettings: Seq[Def.Setting[_]] = inConfig(ProtocConfig)(Seq[Def.Setting[_]](
    protocVersion := "3.6.1",
    protocIncludeStdTypes := true,
    sourceDirectory := {
      (sourceDirectory in configuration).value / "proto"
    },
    sourceDirectories := (sourceDirectory.value :: Nil),
    javaSource := {
      (sourceManaged in configuration).value
    },
    managedClasspath := {
      Classpaths.managedJars(ProtocConfig, classpathTypes.value, update.value)
    },
    protocGrpcVersion := ProtocTasks.getGrpcLatestVersion,
    protocGenerate := {
      val paths = ProtocTasks.compileProto(
        protocVersion.value,
        protocGrpcVersion.value,
        (sourceDirectory in ProtocConfig).value.toPath,
        (javaSource in ProtocConfig).value.toPath,
        protocIncludeStdTypes.value)
      if (paths.nonEmpty) {
        sLog.value.success(s"Compiled proto to ${paths.size} files.")
        paths.foreach(path ⇒ sLog.value.success(path.toString))
      }
      paths.map(_.toFile)
    })) ++ Seq[Setting[_]](
    watchSourcesSetting,
    sourceGenerators in configuration += (protocGenerate in ProtocConfig).taskValue,
    managedSourceDirectories in configuration += (javaSource in ProtocConfig).value,
    libraryDependencies ++= {
      val logger = sLog.value
      val grpcCurrentVersion = (protocGrpcVersion in ProtocConfig).value
      val grpcDependencies = if (ProtocTasks.hasGrpcSource((sourceDirectory in ProtocConfig).value.toPath)) {
        logger.success("Add grpc libs to libraryDependencies")
        Seq(
          "io.grpc" % "grpc-stub" % grpcCurrentVersion,
          "io.grpc" % "grpc-services" % grpcCurrentVersion,
          "io.grpc" % "grpc-netty-shaded" % grpcCurrentVersion,
          "io.grpc" % "grpc-protobuf" % grpcCurrentVersion exclude ("com.google.protobuf", "protobuf-java"))
      } else {
        Seq.empty[ModuleID]
      }
      grpcDependencies :+ "com.google.protobuf" % "protobuf-java" % (protocVersion in ProtocConfig).value
    })

}
