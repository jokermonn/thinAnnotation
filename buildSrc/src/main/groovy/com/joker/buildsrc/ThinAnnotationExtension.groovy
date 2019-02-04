package com.joker.buildsrc;


class ThinAnnotationExtension {

    static String NAME = "thinAnnotation"

    boolean enable = true
    def shrinkClass = [] as HashSet
    def shrinkPackage = ["butterknife/", "android/support/annotation/", "androidx/annotation/"]

    boolean getEnable() {
        return enable
    }

    void shrinkClass(String... shrinkClassName) {
        shrinkClass.addAll(shrinkClassName)
    }

    void shrinkPackage(String... shrinkPackageName) {
        shrinkPackage.addAll(shrinkPackageName)
    }
}
