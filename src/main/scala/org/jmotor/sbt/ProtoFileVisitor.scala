package org.jmotor.sbt

import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.{ FileVisitResult, Path, SimpleFileVisitor }

import scala.collection.mutable.ListBuffer

/**
 *
 * @author AI
 *         2019-06-08
 */
class ProtoFileVisitor extends SimpleFileVisitor[Path] {

  private[this] val files = ListBuffer[Path]()

  override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
    if (file.getFileName.toString.endsWith(".proto")) {
      files += file
    }
    FileVisitResult.CONTINUE
  }

  def result(): Seq[Path] = files

}
