package com.joker.buildsrc.util

import com.android.build.gradle.internal.scope.VariantScope
import com.android.builder.model.AndroidProject
import com.google.common.base.Joiner
import java.io.File
import java.lang.annotation.ElementType

object LogFile {

  private val map = mutableMapOf<String, MutableMap<List<ElementType>, MutableList<String>>>()
  private lateinit var logFile: File

  fun init(variantScope: VariantScope) {
    val output = File(
        Joiner.on(File.separatorChar).join(variantScope.globalScope.buildDir.toString(),
            AndroidProject.FD_OUTPUTS,
            "thinAnnotation",
            variantScope.variantConfiguration.dirName))
    logFile = File(output, "thinAnnotation.txt")
    if (logFile.exists()) {
      logFile.delete()
    }
    output.mkdirs()
    logFile.createNewFile()
  }

  fun setAnnotation(annotationName: String) {
    map[annotationName] = mutableMapOf<List<ElementType>, MutableList<String>>()
  }

  fun setAnnotated(annotationName: String, type: List<ElementType>, message: String) {
    if (!map.containsKey(annotationName)) {
      throw RuntimeException("something error")
    }
    val typeMap = map[annotationName]
    if (!typeMap!!.containsKey(type)) {
      typeMap[type] = mutableListOf<String>()
    }
    val messages = typeMap[type]
    messages!!.add(message)
  }

  fun print() {
    for (entry in map) {
      logFile.appendText("${entry.key}: ")
      for (mutableEntry in entry.value) {
        logFile.appendText("\n  ${mutableEntry.key}: ")
        for (message in mutableEntry.value) {
          logFile.appendText("\n    $message")
        }
      }
      logFile.appendText("\n")
    }
  }
}