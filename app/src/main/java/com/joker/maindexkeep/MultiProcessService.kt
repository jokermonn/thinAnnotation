package com.joker.maindexkeep

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * manifest 中有注释
 */
class MultiProcessService : Service() {

  override fun onBind(intent: Intent): IBinder? {
    // TODO: Return the communication channel to the service.
    throw UnsupportedOperationException("Not yet implemented")
  }
}
