package me.reyoung.YYDroid.main

import android.app.Activity
import me.reyoung.YYDroid.util.LogTag
import android.os.Bundle
import me.reyoung.R

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
class FeedManageActivity extends Activity with LogTag{
  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)

    this.setContentView(R.layout.feed_mgr)
  }
}
