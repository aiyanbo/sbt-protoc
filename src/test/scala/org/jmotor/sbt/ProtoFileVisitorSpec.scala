package org.jmotor.sbt

import java.nio.file.{ Files, Paths }

import org.scalatest.FunSuite

/**
 *
 * @author AI
 *         2019-06-08
 */
class ProtoFileVisitorSpec extends FunSuite {

  test("get all proto files") {
    val visitor = new ProtoFileVisitor()
    Files.walkFileTree(Paths.get("src/sbt-test/sbt-protoc/std-types/src/main"), visitor)
    assert(visitor.result().exists(_.toString == "src/sbt-test/sbt-protoc/std-types/src/main/proto/hello.proto"))
  }

}
