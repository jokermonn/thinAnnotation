package com.joker.maindexkeep.annotations

import android.support.annotation.StringDef
import com.joker.maindexkeep.annotations.AnnotationWrapper.Nothing.Companion.A
import com.joker.maindexkeep.annotations.AnnotationWrapper.Nothing.Companion.B
import com.joker.maindexkeep.model.AnnotationWrapperReference
import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * 由于包含注解内部类，所以本身及本身引用的 [AnnotationWrapperReference]及 [AnnotationWrapperReference]
 * 的引用类都将会被打入 maindex
 */
class AnnotationWrapper {

  fun test() {
    val reference = AnnotationWrapperReference()
  }

  @kotlin.annotation.Retention(SOURCE)
  @StringDef(A, B)
  annotation class Nothing {
    companion object {
      const val A = "a"
      const val B = "b"
    }
  }
}
