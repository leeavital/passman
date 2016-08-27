package com.leeavital.passman.data

import java.io.{IOException, FileOutputStream, File}
import java.nio.file.{Paths, Files}

object IO {
  def writeToFile(bs: Array[Byte], filePath: String): Secure[Unit]= {
    val file = new File(filePath)

    val writer = new FileOutputStream(file)

    writer.write(bs)
    writer.close()
    Ok(Unit)
  }

  def readFileBytes(filePath: String) : Secure[Array[Byte]] = {
    try {
      val bytes = Files.readAllBytes(Paths.get(filePath))
      Ok(bytes)
    } catch {
      case (e: IOException) => CorruptedFile("could not find file")
    }
  }
}
