package me.reyoung

import android.os.Bundle
import android.app.Activity
import android.util.Log

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
class HelloAndroidActivity extends Activity{
  private val TAG = "YYDroid"

  override def onCreate(bundle:Bundle) {
    super.onCreate(bundle)
    Log.i(TAG,"onCreate")
    this.setContentView(R.layout.main)
  }
}
