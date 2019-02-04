package com.joker.maindexkeep.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({ Type.A, Type.B })
public @interface Type {
  String A = "a";
  String B = "b";
}
