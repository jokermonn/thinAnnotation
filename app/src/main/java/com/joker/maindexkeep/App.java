package com.joker.maindexkeep;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.joker.maindexkeep.model.AppReference;

public class App extends MultiDexApplication {
  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    AppReference useless = new AppReference();
  }

  @Override public void onCreate() {
    MultiDex.install(this);
    super.onCreate();
  }
}
