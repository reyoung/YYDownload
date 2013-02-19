package me.reyoung.YYDroid.main

import me.reyoung.YYDroid.util.{DatabaseUtil, LogTag}
import android.os.Bundle
import me.reyoung.R
import android.view.{MenuItem, Menu}
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity
import android.util.Log
import android.widget.EditText
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener

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

  override def onOptionsItemSelected(item:MenuItem)={
    Log.d(LogTag,"On Options Item "+item.getItemId+" Selected")
//    val input = new EditText(this)
    item.getItemId match {
      case R.id.feed_opmenu_add => {
        SubscribeDlg.CallMe(this,this.onNewSubscribeURLRetired)
      }
      case _ => {

      }
    }
    true
  }

  def onNewSubscribeURLRetired(url:String){
    Log.d(LogTag,"On New Subscribe URL Retired "+url)
  }

}
