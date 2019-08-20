package com.joker.maindexkeep.annotations

import kotlin.annotation.AnnotationRetention.BINARY

@kotlin.annotation.Retention(BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class ClassAnn
