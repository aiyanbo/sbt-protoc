package org.jmotor.sbt

import sbt._
import java.io.File

/**
 * Component:
 * Description:
 * Date: 2018/8/8
 *
 * @author AI
 */
trait ProtocKeys {

  lazy val generate: TaskKey[Seq[File]] = taskKey[Seq[File]]("Compile protobuf sources")

  lazy val grpcVersion: SettingKey[String] = settingKey[String]("Get grpc latest version")

  lazy val protocVersion: SettingKey[String] = settingKey[String]("Set protoc version, default 3.6.1")

}
