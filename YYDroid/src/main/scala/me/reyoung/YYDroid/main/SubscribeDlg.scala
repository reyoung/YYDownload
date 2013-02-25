package me.reyoung.YYDroid.main

import me.reyoung.R
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity
import me.reyoung.YYDroid.util.{LogTag, DatabaseUtil}
import android.widget.{EditText, Button}
import android.view.View.OnClickListener
import android.view.View

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/19/13
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */

trait SubscribeDlgCallback extends Activity {
  protected override def onActivityResult(requestId:Int,
                                           status:Int,
                                           data:Intent){
    super.onActivityResult(requestId,status,data)
    requestId match {
      case SubscribeDlg.SUBSCRIBE_REQUEST_ID => {
        val url = status match {
          case Activity.RESULT_OK => {
            data.getExtras.getString("subscribe url")
          }
          case _ => {null}
        }
        this.onSubscribeResult(url)
      }
      case _ => {}
    }
  }
  protected def onSubscribeResult(url:String)
}


object SubscribeDlg {
  val SUBSCRIBE_REQUEST_ID = 2

  def CallMe(ctx:SubscribeDlgCallback):Unit = {
    val intent = new Intent()
    intent.setClass(ctx,classOf[SubscribeDlg])
    ctx.startActivityForResult(intent,SUBSCRIBE_REQUEST_ID)
  }
}

class SubscribeDlg extends OrmLiteBaseActivity[DatabaseUtil]
with QRCodeCallback
with LogTag {
  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)
    this.setContentView(R.layout.feed_mgr_subscribe_dlg)
    val okBtn = this.findViewById(R.id.feed_mgr_subscribe_dlg_okbtn).asInstanceOf[Button]
    okBtn.setOnClickListener(new OnClickListener {
      def onClick(v: View) {
        val intent = packageIntent()
        setResult(Activity.RESULT_OK,intent)
        finish()
      }
    })
    val cancelBtn = this.findViewById(R.id.feed_mgr_subscribe_dlg_cancelbtn).asInstanceOf[Button]
    cancelBtn.setOnClickListener(new OnClickListener {
      def onClick(v: View) {
        setResult(Activity.RESULT_CANCELED,packageIntent())
        finish()
      }
    })
    val act = this
    val qrBtn = this.findViewById(R.id.feed_mgr_subscribe_dlg_qrbtn).asInstanceOf[Button]
    qrBtn.setOnClickListener(new OnClickListener {
      def onClick(v: View) {
        QRCodeActivity.CallMe(act)
      }
    })
  }

  def packageIntent () = {
    val intent = this.getIntent
    intent.putExtra("subscribe url", findViewById(R.id.feed_mgr_subscribe_dlg_edittext)
        .asInstanceOf[EditText].getText.toString)
    intent
  }

  protected def onQRCodeScan(url: String) {
    if (url!=null){
      findViewById(R.id.feed_mgr_subscribe_dlg_edittext).asInstanceOf[EditText].setText(url)
    }
  }
}