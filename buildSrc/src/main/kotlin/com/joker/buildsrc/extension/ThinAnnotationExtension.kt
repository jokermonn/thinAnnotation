package com.joker.buildsrc.extension

import groovy.lang.Closure

open class ThinAnnotationExtension {

  companion object {
    const val NAME = "thinAnnotation"
  }

  var enable = true
  val shrinkClass = mutableSetOf<String>()
  val shrinkPackage = mutableSetOf(
      ShrinkPkgConfig("butterknife/", true),
      ShrinkPkgConfig("android/support/annotation/", false),
      ShrinkPkgConfig("androidx/annotation/", false),
      ShrinkPkgConfig("org/intellij/lang/annotations/", false),
      ShrinkPkgConfig("org/jetbrains/annotations/", false)
  )

  fun enable(enable: Boolean) {
    this.enable = enable
  }

  fun shrinkClass(shrinkClassName: String) {
    shrinkClass.add(shrinkClassName)
  }

  fun shrinkPackage(shrinkPackageName: String) {
    shrinkPackage(shrinkPackageName, null)
  }

  fun shrinkPackage(shrinkPackageName: String, closure: Closure<Boolean>?) {
    shrinkPackage.add(ShrinkPkgConfig(shrinkPackageName, closure?.call() ?: false))
  }

  override fun toString(): String {
    return "ThinAnnotationExtension{" + "enable=" +
        enable +
        ", shrinkClass=" +
        shrinkClass +
        ", shrinkPackage=" +
        shrinkPackage +
        '}'
  }
}