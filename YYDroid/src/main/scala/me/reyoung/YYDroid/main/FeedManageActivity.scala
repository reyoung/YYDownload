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
        new AlertDialog.Builder(FeedManageActivity.this)
          .setTitle("Subscribe")
          .setMessage("Input The Subscribe URL")
        .setView(this.getLayoutInflater.inflate(R.layout.feed_mgr_subscribe_dlg,null))
        .setPositiveButton("OK",new OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {
            val txt = findViewById(R.id.feed_mgr_subscribe_dlg_edittext).asInstanceOf[EditText].getText.toString
            Log.d(LogTag,"Wanna to Subscribe "+txt)
          }
        })
        .setNegativeButton("Cancel", new OnClickListener {
          def onClick(p1: DialogInterface, p2: Int) {}
        }).show()
      }
      case _ => {

      }
    }
    true
  }
}
