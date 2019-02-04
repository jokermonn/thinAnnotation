package com.joker.maindexkeep.annotations;

import android.support.annotation.StringDef;
import com.joker.maindexkeep.model.AnnotationWrapperReference;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.joker.maindexkeep.annotations.AnnotationWrapper.Nothing.A;
import static com.joker.maindexkeep.annotations.AnnotationWrapper.Nothing.B;

/**
 * 由于包含注解内部类，所以本身及本身引用的 {@link AnnotationWrapperReference}及 {@link AnnotationWrapperReference}
 * 的引用类都将会被打入 maindex
 */
public class AnnotationWrapper {

  public void test() {
    AnnotationWrapperReference reference = new AnnotationWrapperReference();
  }

  @Retention(RetentionPolicy.SOURCE)
  @StringDef({ A, B })
  public @interface Nothing {
    String A = "a";
    String B = "b";
  }
}
