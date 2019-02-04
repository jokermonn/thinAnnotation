package com.joker.maindexkeep;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * manifest 中有注释
 */
public class MultiProcessService extends Service {
  public MultiProcessService() {
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
