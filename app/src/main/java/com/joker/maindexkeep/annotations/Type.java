package com.joker.maindexkeep.annotations;

import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@RuntimeAnn
@Retention(RetentionPolicy.RUNTIME)
@StringDef({ Type.A, Type.B })
public @interface Type {
  String A = "a";
  String B = "b";
}
