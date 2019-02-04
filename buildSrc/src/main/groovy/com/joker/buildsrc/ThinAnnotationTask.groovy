package com.joker.buildsrc

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.scope.GlobalScope
import com.android.build.gradle.internal.scope.VariantScope
import com.google.common.base.Joiner
import groovy.io.FileType
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.*
import proguard.obfuscate.MappingReader

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

import static com.android.builder.model.AndroidProject.FD_OUTPUTS

class ThinAnnotationTask extends DefaultTask {

  static final String RETENTION_POLICY = 'Ljava/lang/annotation/RetentionPolicy;'
  static final String SOURCE_TIME = 'SOURCE'

  def mapping = [:]
  def logFile

  @Input
  File jarDir

  @Input
  ApplicationVariantImpl variantImpl

  @Input
  HashSet<String> shrinkClass

  @Input
  List<String> shrinkPackage

  @TaskAction
  void action() {
    Log.log("ThinAnnotation: ThinAnnotationTask start executing...")

    VariantScope variantScope = variantImpl.variantData.scope
    GlobalScope globalScope = variantScope.globalScope

    // get mapping file
    MappingReader reader = new MappingReader(variantImpl.mappingFile)
    reader.pump(new AbstractMappingProcessor() {
      @Override
      boolean processClassMapping(String className, String newClassName) {
        mapping.put(newClassName.replace('.', '/'), className.replace('.', '/'))
        return super.processClassMapping(className, newClassName)
      }
    })
    // create log file
    File output = new File(
        Joiner.on(File.separatorChar).join(String.valueOf(globalScope.getBuildDir()),
            FD_OUTPUTS,
            "thinAnnotation",
            variantScope.getVariantConfiguration().getDirName()))
    logFile = new File(output, "thinAnnotation.txt")
    if (logFile.exists()) {
      logFile.delete()
    }
    output.mkdirs()
    logFile.createNewFile()
    // remove annotations
    jarDir.eachFileRecurse(FileType.FILES) { File file ->
      if (!file.name.endsWith(".jar")) {
        Log.log("ThinAnnotation: ignore file: ${file}")
        return
      }
      Log.log("ThinAnnotation: target file: ${file}")
      JarFile jf = new JarFile(file)
      Enumeration<JarEntry> je = jf.entries()
      File tempJar = new File(file.parentFile, "temp.jar")
      JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempJar))

      while (je.hasMoreElements()) {
        JarEntry jarEntry = je.nextElement()
        ZipEntry zipEntry = new ZipEntry(jarEntry.getName())
        InputStream originIns = jf.getInputStream(jarEntry)
        byte[] bytes = Util.toByteArray(originIns)
        originIns.close()
        if (jarEntry.name.endsWith(".class")) {
          bytes = removeAnnotation(bytes)
        }
        if (bytes != null) {
          jos.putNextEntry(zipEntry)
          jos.write(bytes)
          jos.closeEntry()
        }
      }
      jos.close()
      jf.close()
      file.delete()
      Util.renameFile(tempJar, file)
      Log.log("ThinAnnotation: ThinAnnotationTask is finishing...")
    }
  }

  byte[] removeAnnotation(byte[] bytes) {
    ClassReader cr = new ClassReader(bytes)
    ClassWriter cw = new ClassWriter(cr, 0)
    String className = ""
    boolean canRemoved = false
    ClassVisitor cv = new ClassVisitor(Opcodes.ASM4, cw) {
      private boolean isAnnotation

      @Override
      void visit(int version, int access, String name, String signature, String superName,
          String[] interfaces) {
        className = name
        isAnnotation = (access & Opcodes.ACC_ANNOTATION) != 0
        super.visit(version, access, name, signature, superName, interfaces)
      }

      @Override
      MethodVisitor visitMethod(int access, String name, String desc, String signature,
          String[] exceptions) {

        return new MethodVisitor(Opcodes.ASM4,
            super.visitMethod(access, name, desc, signature, exceptions)) {
          // ElementType.METHOD / ElementType.CONSTRUCTOR
          @Override
          AnnotationVisitor visitAnnotation(String ann, boolean visible) {
            if (needRemove(desc2ClassPath(ann))) {
              return null
            }
            return super.visitAnnotation(ann, visible)
          }

          // ElementType.PARAMETER
          @Override
          AnnotationVisitor visitParameterAnnotation(int parameter, String ann,
              boolean visible) {
            if (needRemove(desc2ClassPath(ann))) {
              return null
            }
            return super.visitParameterAnnotation(parameter, ann, visible)
          }
        }
      }

      // ElementType.FIELD / LOCAL_VARIABLE
      @Override
      FieldVisitor visitField(int access, String name, String desc, String signature,
          Object value) {
        return new FieldVisitor(Opcodes.ASM4,
            super.visitField(access, name, desc, signature, value)) {
          @Override
          AnnotationVisitor visitAnnotation(String ann, boolean visible) {
            if (needRemove(desc2ClassPath(ann))) {
              return null
            }
            return super.visitAnnotation(ann, visible)
          }
        }
      }

      @Override
      AnnotationVisitor visitAnnotation(String ann, boolean visible) {
        // ElementType.TYPE / ElementType.ANNOTATION_TYPE
        if (needRemove(desc2ClassPath(ann))) {
          return null
        }
        // 是目标注解类则要删除类文件
        if (isAnnotation) {
          return new AnnotationVisitor(Opcodes.ASM4, super.visitAnnotation(ann, visible)) {
            @Override
            void visitEnum(String name, String desc1, String value) {
              // RetentionPolicy 为 SOURCE
              if (desc1 == RETENTION_POLICY) {
                if (value == SOURCE_TIME) {
                  canRemoved = true
                  logFile << "annotation(SOURCE) removed => ${decodeProguard(cr.className)}\n"
                } else {
                  if (needRemove(cr.className)) {
                    canRemoved = true
                    logFile << "annotation(${value}) removed => ${decodeProguard(cr.className)}\n"
                  } else {
                    logFile << "annotation(${value}) not removed => ${decodeProguard(cr.className)}\n"
                  }
                }
              }
              super.visitEnum(name, desc1, value)
            }
          }
        }
        return super.visitAnnotation(ann, visible)
      }
    }
    cr.accept(cv, 0)
    if (canRemoved) {
      return null
    }
    return cw.toByteArray()
  }

  private boolean needRemove(String className) {
    def newName = decodeProguard(className)
    return shrinkPackage.any { newName.startsWith(it) } || shrinkClass.contains(newName)
  }

  private String desc2ClassPath(String desc) {
    if (new String(desc.charAt(0)) == 'L' && new String(desc.charAt(desc.length() - 1)) == ';') {
      return desc.substring(1, desc.length() - 1)
    } else {
      throw new IllegalStateException("desc ${desc} is not a descriptor")
    }
  }

  /**
   * return class name before proguard
   * @param the class name after proguard , like android.arch.core.a.a
   * @return return origin class name according mapping.txt, return {@param name} if null
   */
  private String decodeProguard(String name) {
    def decodeName = mapping.get(name)
    return decodeName != null ? decodeName : name
  }
}
