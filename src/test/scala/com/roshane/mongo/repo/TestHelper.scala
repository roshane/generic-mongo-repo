package com.roshane.mongo.repo

import scala.io.Source

object TestHelper {

  def readFileAsString(path: String): String = {
    val filePath = getClass.getResource(path)
    val file = Source.fromFile(filePath.getFile)
    val content = file.mkString
    file.close()
    content
  }
}
