package com.joker.buildsrc.core

import com.joker.buildsrc.util.LogFile
import com.joker.buildsrc.util.MappingUtil
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.annotation.ElementType
import java.lang.annotation.ElementType.ANNOTATION_TYPE
import java.lang.annotation.ElementType.LOCAL_VARIABLE
import java.lang.annotation.ElementType.PARAMETER
import java.util.Collections

class ClzClassVisitor(classWriter: ClassWriter,
    private val clzName: String,
    private val shrinkClass: MutableSet<String>) : ClassVisitor(Opcodes.ASM4, classWriter) {

  override fun visitMethod(access: Int, name: String, desc: String, signature: String?,
      exceptions: Array<String>?): MethodVisitor {
    return object : MethodVisitor(Opcodes.ASM4,
        super.visitMethod(access, name, desc, signature, exceptions)) {

      // ElementType.METHOD / ElementType.CONSTRUCTOR
      override fun visitAnnotation(ann: String, visible: Boolean): AnnotationVisitor? {
        if (needRemove(desc2ClassPath(ann))) {
          LogFile.setAnnotated(
              MappingUtil.decodeProguard(desc2ClassPath(ann)),
              listOf(ElementType.METHOD, ElementType.CONSTRUCTOR),
              "${decodeProguard(clzName)}#${decodeProguardMethod(clzName, name)}"
          )
          return null
        }
        return super.visitAnnotation(ann, visible)
      }

      // ElementType.PARAMETER
      override fun visitParameterAnnotation(parameter: Int, ann: String,
          visible: Boolean): AnnotationVisitor? {
        if (needRemove(desc2ClassPath(ann))) {
          LogFile.setAnnotated(
              decodeProguard(desc2ClassPath(ann)),
              Collections.singletonList(PARAMETER),
              "${decodeProguard(clzName)}#${decodeProguardMethod(clzName, name)}#$parameter"
          )
          return null
        }
        return super.visitParameterAnnotation(parameter, ann, visible)
      }
    }
  }

  // ElementType.FIELD / LOCAL_VARIABLE
  override fun visitField(access: Int, name: String?, desc: String?, signature: String?,
      value: Any?): FieldVisitor {
    return object : FieldVisitor(Opcodes.ASM4,
        super.visitField(access, name, desc, signature, value)) {
      override fun visitAnnotation(ann: String, visible: Boolean): AnnotationVisitor? {
        if (needRemove(desc2ClassPath(ann))) {
          LogFile.setAnnotated(
              decodeProguard(desc2ClassPath(ann)),
              listOf(ElementType.FIELD, LOCAL_VARIABLE),
              "${decodeProguard(clzName)}#${decodeProguardField(decodeProguard(clzName), name!!)}"
          )
          return null
        }
        return super.visitAnnotation(ann, visible)
      }
    }
  }

  override fun visitAnnotation(ann: String, visible: Boolean): AnnotationVisitor? {
    // ElementType.TYPE / ElementType.ANNOTATION_TYPE
    if (needRemove(desc2ClassPath(ann))) {
      LogFile.setAnnotated(
          decodeProguard(desc2ClassPath(ann)),
          listOf(ANNOTATION_TYPE),
          decodeProguard(clzName)
      )
      return null
    }
    return super.visitAnnotation(ann, visible)
  }

  private fun desc2ClassPath(desc: String): String {
    when {
      desc[0] == 'L' && desc[desc.length - 1] == ';' -> return desc.substring(1, desc.length - 1)
      else -> throw IllegalStateException("desc $desc is not a descriptor")
    }
  }

  private fun needRemove(className: String): Boolean {
    val newName = decodeProguard(className)
    return shrinkClass.contains(newName)
  }

  private fun decodeProguard(className: String) = MappingUtil.decodeProguard(className)

  private fun decodeProguardField(className: String, filedName: String) = MappingUtil.decodeProguardField(className, filedName!!)

  private fun decodeProguardMethod(className: String, methodName: String) = MappingUtil.decodeProguardMethod(className, methodName!!)
}