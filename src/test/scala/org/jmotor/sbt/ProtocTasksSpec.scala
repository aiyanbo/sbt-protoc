package org.jmotor.sbt

import java.nio.file.{ Files, Paths }

import org.scalatest.FunSuite

import scala.util.Properties

/**
 * Component:
 * Description:
 * Date: 2018/8/11
 *
 * @author AI
 */
class ProtocTasksSpec extends FunSuite {

  test("compile proto") {
    val sourceDirectory = Paths.get(Properties.userDir, "src", "test", "proto")
    val javaOutDirectory = Files.createTempDirectory("protobuf")
    val version = ProtocTasks.getGrpcLatestVersion
    val protocVersion = ProtocTasks.getProtocLatestVersion
    val javas = ProtocTasks.compileProto(protocVersion, version, sourceDirectory, javaOutDirectory, includeStdTypes = true)
    Seq("HelloServiceGrpc.java", "HelloServiceProto.java").foreach { name ⇒
      assert(javas.exists(_.getFileName.toString == name))
    }
  }

}
