package com.joker.buildsrc.util

import proguard.obfuscate.MappingProcessor
import proguard.obfuscate.MappingReader
import java.io.File

object MappingUtil {
  private val classMapping = mutableMapOf<String, String>()
  private val fieldMapping = mutableMapOf<String, String>()
  private val methodMapping = mutableMapOf<String, String>()

  /**
   * return class name before proguard
   * @param name: the class name after proguard , like android.arch.core.a.a
   * @return return origin class name according mapping.txt, return {@param name} if null
   */
  fun decodeProguard(name: String): String = classMapping.getOrDefault(name, "")

  fun decodeProguardField(className: String, fieldName: String) = fieldMapping.getOrDefault(
      "$className|$fieldName", "")

  fun decodeProguardMethod(className: String, methodName: String) = methodMapping.getOrDefault(
      "${decodeProguard(className)}|$methodName", "")

  fun init(file: File) {
    val reader = MappingReader(file)
    reader.pump(object : MappingProcessor {
      override fun processClassMapping(className: String?,
          newClassName: String?): Boolean {
        classMapping[newClassName!!.replace('.', '/')] = className!!.replace('.', '/')
        return true
      }

      //  BUG: oldClassName same with newClassName, both oldClassName
      override fun processFieldMapping(oldClassName: String?, filedType: String?,
          fieldName: String?,
          newClassName: String?,
          newFieldName: String?) {
        fieldMapping["${newClassName!!.replace('.', '/')}|$newFieldName"] = fieldName!!
      }

      //  BUG: oldClassName same with newClassName, both newClassName
      override fun processMethodMapping(oldClassName: String?, firstLineNumber: Int,
          lastLineNumber: Int, methodReturnType: String?, oldMethodName: String?,
          methodArguments: String?, newClassName: String?, newFirstLineNumber: Int,
          newLastLineNumber: Int, newMethodName: String?) {
        methodMapping["${newClassName!!.replace('.', '/')}|$newMethodName"] = oldMethodName!!
      }
    })
  }
}