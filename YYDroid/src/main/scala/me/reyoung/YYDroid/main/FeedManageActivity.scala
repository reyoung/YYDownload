package me.reyoung.YYDroid.main

import me.reyoung.YYDroid.util.{DatabaseUtil, LogTag}
import android.os.Bundle
import me.reyoung.R
import android.view.{MenuItem, Menu}
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity
import android.util.Log
import me.reyoung.YYDroid.yydownload.{AndroidParserFactory, AndYoukuAuthorSubscriber}
import android.widget.Toast

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
class FeedManageActivity extends OrmLiteBaseActivity[DatabaseUtil]
  with SubscribeDlgCallback
  with LogTag
{
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
        SubscribeDlg.CallMe(this)
      }
      case _ => {

      }
    }
    true
  }

  def onNewSubscribeURLRetired(url:String){
    Log.d(LogTag,"On New Subscribe URL Retired "+url)
  }

  protected def onSubscribeResult(url: String) {
    Log.d(LogTag,"On Subscribe URL "+url)

    val result = AndroidParserFactory.parseSubscribe(url)
    if (result!=null){
      // Append Subscribe
    } else {
      Toast.makeText(this,R.string.subscribe_fails,Toast.LENGTH_LONG).show()
    }
  }
}
