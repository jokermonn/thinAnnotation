package com.joker.buildsrc.core

import com.joker.buildsrc.extension.ShrinkPkgConfig
import com.joker.buildsrc.util.LogFile
import com.joker.buildsrc.util.MappingUtil
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class ClassFile(private val bytes: ByteArray?) {

  internal val isAnnotation: Boolean

  init {
    val cr = ClassReader(bytes)
    isAnnotation = cr.access and Opcodes.ACC_ANNOTATION != 0
  }

  fun process(shrinkClasses: MutableSet<String>, shrinkPackage: Set<ShrinkPkgConfig>): ByteArray? {
    val cr = ClassReader(bytes)
    val cw = ClassWriter(cr, 0)
    return if (isAnnotation) {
      LogFile.setAnnotation(MappingUtil.decodeProguard(cr.className))

      val cv = AnnotationClassVisitor(cw, cr.className, shrinkClasses, shrinkPackage)
      cr.accept(cv, 0)
      if (cv.canRemoved) null else cw.toByteArray()
    } else {
      val cv = ClzClassVisitor(cw, cr.className, shrinkClasses)
      cr.accept(cv, 0)
      cw.toByteArray()
    }
  }
}