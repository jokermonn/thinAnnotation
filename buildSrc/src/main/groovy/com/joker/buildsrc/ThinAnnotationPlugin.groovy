package com.joker.buildsrc

import com.android.build.gradle.internal.api.ApplicationVariantImpl
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class ThinAnnotationPlugin implements Plugin<Project> {

  def shrinkClass = [] as HashSet
  def shrinkPackage = []

  @Override
  void apply(Project project) {

    final ThinAnnotationExtension extension = project.extensions.create(
        ThinAnnotationExtension.NAME, ThinAnnotationExtension)

    project.afterEvaluate {
      project.plugins.withId('com.android.application') {
        project.android.applicationVariants.all { ApplicationVariantImpl variant ->
          if (extension.enable) {
            Log.log("ThinAnnotation: ThinAnnotationTask is enabled")
            def varNameCap = variant.name.capitalize()
            def proguardTask = project.tasks.findByName(
                "transformClassesAndResourcesWithProguardFor${varNameCap}")

            if (!proguardTask) {
              return
            }

            proguardTask.doLast() {
              proguardTask.outputs.files.each { File file ->
                if (file.isDirectory()) {
                  Log.log("ThinAnnotation: extension: ${extension}")

                  DefaultTask thinAnnTask = project.tasks.create(
                      name: "thinAnnotationFor${varNameCap}",
                      type: ThinAnnotationTask) {
                    jarDir = file
                    variantImpl = variant
                    shrinkClass = extension.shrinkClass
                    shrinkPackage = extension.shrinkPackage
                  }
                  thinAnnTask.execute()
                }
              }
            }
          }
        }
      }
    }
  }
}

