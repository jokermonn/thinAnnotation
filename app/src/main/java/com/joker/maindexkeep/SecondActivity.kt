package com.joker.maindexkeep

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import butterknife.BindView
import com.joker.maindexkeep.annotations.RuntimeAnn

/**
 * [BindView] 是运行时注解，所以当前类将会被打入 maindex
 */
class SecondActivity : AppCompatActivity() {

  @BindView(R.id.tv)
  internal var tv: TextView? = null

  @RuntimeAnn
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_second)
  }
}
