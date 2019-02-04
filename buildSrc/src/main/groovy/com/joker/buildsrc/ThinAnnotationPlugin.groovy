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

    ThinAnnotationExtension extension = project.extensions.create(ThinAnnotationExtension.NAME,
        ThinAnnotationExtension)

    project.afterEvaluate {
      project.plugins.withId('com.android.application') {
        if (extension.enable) {
          project.android.applicationVariants.all { ApplicationVariantImpl variant ->
            def varNameCap = variant.name.capitalize()
            def proguardTask = project.tasks.findByName(
                "transformClassesAndResourcesWithProguardFor${varNameCap}")

            if (!proguardTask) {
              return
            }

            proguardTask.doLast() {
              shrinkClass.clear()
              shrinkClass.addAll(extension.shrinkClass)
              shrinkPackage.clear()
              shrinkPackage.addAll(extension.shrinkPackage)

              proguardTask.outputs.files.each { File file ->
                if (file.isDirectory()) {
                  DefaultTask thinAnnTask = project.tasks.create(
                      name: "thinAnnotationFor${varNameCap}",
                      type: ThinAnnotationTask) {
                    jarDir = file
                    variantImpl = variant
                    shrinkClass = this.shrinkClass
                    shrinkPackage = this.shrinkPackage
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

