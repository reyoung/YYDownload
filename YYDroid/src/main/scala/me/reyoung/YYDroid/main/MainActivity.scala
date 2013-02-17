package me.reyoung.YYDroid.main

import me.reyoung.YYDroid.util.{DatabaseUtil, LogTag}
import android.os.Bundle
import android.util.Log
import android.app.Activity
import me.reyoung.R
import android.widget.Button
import android.view.View.OnClickListener
import android.view.View
import android.content.{Context, Intent}
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
class MainActivity extends OrmLiteBaseActivity[DatabaseUtil] with LogTag{


  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)
    Log.d(LogTag,"On Create")
//    val ctor = classOf[DatabaseUtil].getConstructor(classOf[Context])
//    Log.i(LogTag,"Constructor Method "+ctor)
//    val tmp = ctor.newInstance(this)
//    Log.i(LogTag,"New Instance Is "+ tmp)
    this.setContentView(R.layout.main)
    val act = this
    val NewQRCode = this.findViewById(R.id.main_new_qr_code).asInstanceOf[Button]
    NewQRCode.setOnClickListener(new OnClickListener{
      def onClick(view: View) {
        Log.d(LogTag,"On QR Code Click")

        /**
         * @todo QRCode Scan
         */
      }
    } )

    val DM = this.findViewById(R.id.main_download_management).asInstanceOf[Button]
    DM.setOnClickListener(new OnClickListener {
      def onClick(view: View) {
        Log.d(LogTag,"On Download Management Click")

        /**
         * @todo Download Management Redirect
         */
      }
    })

    val FM = this.findViewById(R.id.main_feed_management).asInstanceOf[Button]
    FM.setOnClickListener(new OnClickListener {
      def onClick(view: View) {
        Log.d(LogTag,"On Feed Management Click")
        val intent = new Intent()
        intent.setClass(act,classOf[FeedManageActivity])
        act.startActivity(intent)
      }

    })

    val about = this.findViewById(R.id.main_about_me).asInstanceOf[Button]
    about.setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        Log.d(LogTag,"On About Me Click")
      }
    })
  }
}
