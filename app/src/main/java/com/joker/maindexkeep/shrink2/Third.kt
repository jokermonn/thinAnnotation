package com.joker.maindexkeep.shrink2

import kotlin.annotation.AnnotationRetention.RUNTIME

@kotlin.annotation.Retention(RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER)
annotation class Third