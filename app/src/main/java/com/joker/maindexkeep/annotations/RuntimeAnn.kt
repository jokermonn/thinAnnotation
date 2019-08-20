package com.joker.maindexkeep.annotations

import kotlin.annotation.AnnotationRetention.RUNTIME

@kotlin.annotation.Retention(RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS, AnnotationTarget.FILE,
    AnnotationTarget.ANNOTATION_CLASS)
annotation class RuntimeAnn
