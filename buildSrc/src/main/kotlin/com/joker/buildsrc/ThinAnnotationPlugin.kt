package com.joker.buildsrc

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.joker.buildsrc.extension.ThinAnnotationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel.DEBUG

class ThinAnnotationPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val extension = project.extensions.create(
        ThinAnnotationExtension.NAME, ThinAnnotationExtension::class.java)

    project.afterEvaluate {
      if (project.plugins.hasPlugin("com.android.application")) {
        project.extensions.getByType(AppExtension::class.java).applicationVariants.all { variant ->
          if (extension.enable) {
            project.logger.log(DEBUG, "ThinAnnotation: ThinAnnotation is enabled")

            val varNameCap = variant.name.capitalize()
            val proguardTask = project.tasks.findByName(
                "transformClassesAndResourcesWithProguardFor$varNameCap")
            if (proguardTask?.enabled == true) {
              val thinAnnTask = project.tasks.create(
                  "thinAnnotationFor$varNameCap",
                  ThinAnnotationTask::class.java) {
                it.variantImpl = variant as ApplicationVariantImpl
                it.shrinkClass = extension.shrinkClass
                it.shrinkPackage = extension.shrinkPackage
              }

              proguardTask.finalizedBy(thinAnnTask)
            }
          }
        }
      }
    }
  }
}