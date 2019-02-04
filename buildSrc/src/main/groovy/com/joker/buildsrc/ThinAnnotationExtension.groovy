package com.joker.buildsrc;

class ThinAnnotationExtension {

  static String NAME = "thinAnnotation"

  private boolean enable = true
  private def shrinkClass = [] as HashSet
  private def shrinkPackage = ["butterknife/", "android/support/annotation/", "androidx/annotation/"]

  def getShrinkClass() {
    return shrinkClass
  }

  def getShrinkPackage() {
    return shrinkPackage
  }

  boolean getEnable() {
    return enable
  }

  void setEnable(boolean enable) {
    this.enable = enable
  }

  void setShrinkClass(String... shrinkClassName) {
    shrinkClass.addAll(shrinkClassName)
  }

  void setShrinkPackage(String... shrinkPackageName) {
    shrinkPackage.addAll(shrinkPackageName)
  }

  @Override String toString() {
    return "ThinAnnotationExtension{" + "enable=" +
        enable +
        ", shrinkClass=" +
        shrinkClass +
        ", shrinkPackage=" +
        shrinkPackage +
        '}';
  }
}
