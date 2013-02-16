package me.reyoung.YYDroid.main

import android.app.Activity
import me.reyoung.YYDroid.util.{DatabaseUtil, LogTag}
import android.os.Bundle
import me.reyoung.R
import android.view.Menu
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
class FeedManageActivity extends OrmLiteBaseActivity[DatabaseUtil]  with LogTag{
  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)
    this.setContentView(R.layout.feed_mgr)
  }

  override def onCreateOptionsMenu(menu:Menu)={
    getMenuInflater.inflate(R.menu.feed_option_menu,menu)
    true
  }
}
