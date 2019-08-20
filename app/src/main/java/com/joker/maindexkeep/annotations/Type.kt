package com.joker.maindexkeep.annotations

import android.support.annotation.StringDef
import kotlin.annotation.AnnotationRetention.RUNTIME

@RuntimeAnn
@kotlin.annotation.Retention(RUNTIME)
@StringDef(Type.A, Type.B)
annotation class Type {
  companion object {
    const val A = "a"
    const val B = "b"
  }
}
