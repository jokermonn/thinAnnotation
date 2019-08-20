package com.joker.maindexkeep.model

import com.google.gson.annotations.SerializedName

/**
 * 由于 [SerializedName] 是运行时注解，所以当前类将会被打入 maindex
 */
class Person {
  @SerializedName("i")
  private val i: Int = 0
}
