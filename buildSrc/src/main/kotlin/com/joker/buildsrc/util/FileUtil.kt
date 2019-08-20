package com.joker.buildsrc.util

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

object FileUtil {
  @Throws(IOException::class)
  internal fun toByteArray(input: InputStream): ByteArray {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(8024)
    var n = 0
    var count: Long = 0
    while (-1 != n) {
      output.write(buffer, 0, n)
      count += n.toLong()
      n = input.read(buffer)
    }
    return output.toByteArray()
  }

  internal fun renameFile(originFile: File, targetFile: File) {
    if (targetFile.exists()) {
      targetFile.delete()
    }
    targetFile.parentFile.mkdirs()
    if (!originFile.renameTo(targetFile)) {
      throw RuntimeException("\${originFile} rename to \${targetFile} failed ")
    }
  }
}
