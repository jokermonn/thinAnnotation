package com.joker.buildsrc

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.joker.buildsrc.core.ClassFile
import com.joker.buildsrc.extension.ShrinkPkgConfig
import com.joker.buildsrc.util.LogFile
import com.joker.buildsrc.util.MappingUtil
import com.joker.buildsrc.util.FileUtil
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel.DEBUG
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

open class ThinAnnotationTask : DefaultTask() {

  @Input
  lateinit var variantImpl: ApplicationVariantImpl

  @Input
  lateinit var shrinkClass: MutableSet<String>

  @Input
  lateinit var shrinkPackage: MutableSet<ShrinkPkgConfig>

  @Throws(IOException::class)
  @TaskAction
  fun action() {
    val logger = project.logger
    val variantScope = variantImpl.variantData.scope

    logger.log(DEBUG, "ThinAnnotation: ThinAnnotationTask start executing...")

    // create log file
    LogFile.init(variantScope)

    // get mapping file
    MappingUtil.init(variantImpl.mappingFile)

    // remove annotations
    val proguardTask = project.tasks.findByName(
        "transformClassesAndResourcesWithProguardFor${variantImpl.name.capitalize()}")
    val file = proguardTask!!.outputs.files.files.find { s -> s.isDirectory }!!.walkTopDown()
        .maxDepth(1).filter { it.name.endsWith(".jar") }.first()
    process(file)

    LogFile.print()

    logger.log(DEBUG, "ThinAnnotation: ThinAnnotationTask is finishing...")
  }

  private fun process(file: File) {
    // scan annotation
    var jf = JarFile(file)

    var tempJar = File(file.parentFile, "temp.jar")
    var jos = JarOutputStream(FileOutputStream(tempJar))
    var je = jf.entries()

    for (jarEntry in je.iterator()) {
      val zipEntry = ZipEntry(jarEntry.name)
      val originIns = jf.getInputStream(jarEntry)
      var annBytes: ByteArray? = FileUtil.toByteArray(originIns)
      originIns.close()
      if (jarEntry.name.endsWith(".class")) {
        val classFile = ClassFile(annBytes)
        if (classFile.isAnnotation) {
          annBytes = classFile.process(shrinkClass, shrinkPackage)
        }
      }
      annBytes?.let {
        jos.putNextEntry(zipEntry)
        jos.write(annBytes)
        jos.closeEntry()
      }
    }
    jos.close()
    jf.close()

    // scan class
    jf = JarFile(tempJar)
    tempJar = File(file.parentFile, "temp2.jar")
    jos = JarOutputStream(FileOutputStream(tempJar))
    je = jf.entries()

    for (jarEntry in je.iterator()) {
      val zipEntry = ZipEntry(jarEntry.name)
      val originIns = jf.getInputStream(jarEntry)
      var clzBytes: ByteArray? = FileUtil.toByteArray(originIns)
      originIns.close()
      if (jarEntry.name.endsWith(".class")) {
        val classFile = ClassFile(clzBytes)
        if (!classFile.isAnnotation) {
          clzBytes = classFile.process(shrinkClass, shrinkPackage)
        }
      }
      clzBytes?.let {
        jos.putNextEntry(zipEntry)
        jos.write(clzBytes)
        jos.closeEntry()
      }
    }

    jos.close()
    jf.close()
    file.delete()
    FileUtil.renameFile(tempJar, file)
  }
}