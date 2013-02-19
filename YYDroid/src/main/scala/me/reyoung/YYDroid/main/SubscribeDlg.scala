package me.reyoung.YYDroid.main

import me.reyoung.R
import android.app.Activity
import android.os.Bundle
import android.content.DialogInterface
import android.content.DialogInterface.OnKeyListener
import android.view.{View, KeyEvent}
import android.app.AlertDialog.Builder
import android.widget.{EditText, Button}

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/19/13
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */

object SubscribeDlg {
  def CallMe(ctx:Activity,callback:(String)=>Unit):Unit = {
    val dlg = new Builder(ctx)
    dlg.setMessage(R.string.subscribe_dlg_activity_title)
    val view = ctx.getLayoutInflater.inflate(
      R.layout.feed_mgr_subscribe_dlg,
      null
    )
    val qrBtn = view.findViewById(R.id.feed_mgr_subscribe_dlg_qrbtn).asInstanceOf[Button]
    val txtEdit = view.findViewById(R.id.feed_mgr_subscribe_dlg_edittext).asInstanceOf[EditText]
//    var url:String = null
    dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener {
      def onClick(p1: DialogInterface, p2: Int) {
        callback(txtEdit.getText.toString)
      }
    })
    dlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener {
      def onClick(p1: DialogInterface, p2: Int) {
        callback(null)
      }
    })
    qrBtn.setOnClickListener(new View.OnClickListener{
      def onClick(v: View) {
        txtEdit.setText("Stub!!")
      }
    })
    dlg.setView(view)
    dlg.show()
  }
}
//class SubscribeDlg(ctx:Activity) extends android.app.Dialog(ctx){
//
//  override def onCreate(bundle:Bundle){
//    super.onCreate(bundle)
//    this.setContentView(R.layout.feed_mgr_subscribe_dlg)
//  }
//}
