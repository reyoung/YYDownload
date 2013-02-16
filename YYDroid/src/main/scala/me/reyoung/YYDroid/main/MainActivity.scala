package me.reyoung.YYDroid.main

import me.reyoung.YYDroid.util.LogTag
import android.os.Bundle
import android.util.Log
import android.app.Activity
import me.reyoung.R

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
class MainActivity extends Activity with LogTag{

  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)
    Log.d(LogTag,"On Create")
    this.setContentView(R.layout.main)
  }
}
