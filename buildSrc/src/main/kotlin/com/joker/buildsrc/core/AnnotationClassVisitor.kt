package com.joker.buildsrc.core

import com.joker.buildsrc.extension.ShrinkPkgConfig
import com.joker.buildsrc.util.MappingUtil
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class AnnotationClassVisitor(classWriter: ClassWriter, val clzName: String,
    private val shrinkClass: MutableSet<String>,
    private val shrinkPackage: Set<ShrinkPkgConfig>) :
    ClassVisitor(Opcodes.ASM4, classWriter) {

  private companion object {
    private const val RETENTION_POLICY = "Ljava/lang/annotation/RetentionPolicy;"
    private const val SOURCE = "SOURCE"
    private const val RUNTIME = "RUNTIME"
  }

  var canRemoved: Boolean = false

  override fun visitAnnotation(ann: String, visible: Boolean): AnnotationVisitor? {
    return object : AnnotationVisitor(Opcodes.ASM4, super.visitAnnotation(ann, visible)) {
      override fun visitEnum(name: String?, desc1: String?, value: String?) {
        // RetentionPolicy 为 SOURCE
        if (desc1 == RETENTION_POLICY) {
          if (value == SOURCE) {
            canRemoved = true
          } else if (needRemove(clzName, value == RUNTIME)) {
            canRemoved = true
          }
        }
        super.visitEnum(name, desc1, value)
      }
    }
  }

  private fun needRemove(className: String, isRuntime: Boolean): Boolean {
    val newName = MappingUtil.decodeProguard(className)

    val any = shrinkPackage.find {
      newName.startsWith(it.pkgName) && if (isRuntime) it.ignoreRuntime else true
    }
    any?.let { shrinkClass.add(newName) }
    return any != null || shrinkClass.contains(newName)
  }
}