package com.joker.maindexkeep.model;

import com.google.gson.annotations.SerializedName;

/**
 * 由于 {@link SerializedName} 是运行时注解，所以当前类将会被打入 maindex
 */
public class Person {
  @SerializedName("i") private int i;
}
