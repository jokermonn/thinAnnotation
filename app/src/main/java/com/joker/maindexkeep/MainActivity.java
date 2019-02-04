package com.joker.maindexkeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.joker.maindexkeep.annotations.RuntimeAnn;

/**
 * 方法/类被运行时注解所修饰，所以当前类将会被打入 maindex
 */
@RuntimeAnn
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @RuntimeAnn
  void test() {

  }
}
