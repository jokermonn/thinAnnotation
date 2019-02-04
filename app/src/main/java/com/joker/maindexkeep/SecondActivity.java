package com.joker.maindexkeep;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import butterknife.BindView;
import com.joker.maindexkeep.annotations.RuntimeAnn;

/**
 * {@link BindView} 是运行时注解，所以当前类将会被打入 maindex
 */
public class SecondActivity extends AppCompatActivity {

  @BindView(R.id.tv) TextView tv;

  @Override @RuntimeAnn
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_second);
  }
}
