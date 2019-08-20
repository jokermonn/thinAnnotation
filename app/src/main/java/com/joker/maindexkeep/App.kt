package com.joker.maindexkeep

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.util.Log
import com.joker.maindexkeep.model.AppReference

class App : MultiDexApplication() {
  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    val useless = AppReference()
  }

  override fun onCreate() {
    MultiDex.install(this)
    super.onCreate()
  }
}
