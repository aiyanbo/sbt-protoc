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

class ScopedProtocPlugin(configuration: Configuration, postfix: String = "") extends AutoPlugin {

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
    sourceDirectory := {
      (sourceDirectory in configuration).value / "proto"
    },
    sourceDirectories := (sourceDirectory.value :: Nil),
    javaSource := {
      (sourceManaged in configuration).value / "compiled_protobuf"
    },
    managedClasspath := {
      Classpaths.managedJars(ProtocConfig, classpathTypes.value, update.value)
    },
    grpcVersion := ProtocTasks.getGrpcLatestVersion,
    generate := {
      val paths = ProtocTasks.compileProto(
        protocVersion.value,
        grpcVersion.value,
        (sourceDirectory in ProtocConfig).value.toPath,
        (javaSource in ProtocConfig).value.toPath)
      if (paths.isEmpty) {
        sLog.value.warn("Cannot find proto sources compile.")
      }
      paths.map(_.toFile)
    })) ++ Seq[Setting[_]](
    watchSourcesSetting,
    sourceGenerators in configuration += (generate in ProtocConfig).taskValue,
    managedSourceDirectories in configuration += (javaSource in ProtocConfig).value,
    libraryDependencies ++= {
      val grpcCurrentVersion = (grpcVersion in ProtocConfig).value
      Seq(
        "io.grpc" % "grpc-core" % grpcCurrentVersion,
        "io.grpc" % "grpc-stub" % grpcCurrentVersion,
        "io.grpc" % "grpc-protobuf" % grpcCurrentVersion exclude ("com.google.protobuf", "protobuf-java"),
        "com.google.protobuf" % "protobuf-java" % (protocVersion in ProtocConfig).value)
    })

}
