package me.reyoung.YYDroid.main

import me.reyoung.R
import android.app.Activity
import android.os.Bundle

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/19/13
 * Time: 8:22 PM
 * To change this template use File | Settings | File Templates.
 */

object SubscribeDlgActivity {
  def CallMe(ctx:Activity):String = {
//    val dlg = new Builder(ctx)
//    dlg.setMessage(R.string.subscribe_dlg_activity_title)
//    dlg.setView(ctx.getLayoutInflater.inflate(
//      R.layout.feed_mgr_subscribe_dlg,
//      null
//    ))
//
//    dlg.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener {
//      def onClick(p1: DialogInterface, p2: Int) {
//
//      }
//    })
//    dlg.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener {
//      def onClick(p1: DialogInterface, p2: Int) {
//
//      }
//    })
    val tmp = new SubscribeDlg(ctx)
    tmp.show()
//    tmp.setOnKeyListener()
    null
  }
}
class SubscribeDlg(ctx:Activity) extends android.app.Dialog(ctx){

  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)
    this.setContentView(R.layout.feed_mgr_subscribe_dlg)
  }
}
//class SubscribeDlgActivity extends OrmLiteBaseActivity[DatabaseUtil] with LogTag{
//  override def onCreate(bundle:Bundle) {
//    super.onCreate(bundle)
//    this.setContentView(R.layout.feed_mgr_subscribe_dlg)
//
//    val okBtn = this.findViewById(R.id.subscribe_dlg_ok_btn).asInstanceOf[Button]
//    val cancelBtn = this.findViewById(R.id.subscribe_dlg_cancle_btn).asInstanceOf[Button]
//    val onBtnClick = new OnClickListener {
//      def onClick(btn: View) {
//        val url = btn.getId match {
//          case R.id.subscribe_dlg_ok_btn => {
//            findViewById(R.id.feed_mgr_subscribe_dlg_edittext).asInstanceOf[EditText].getText.toString
//          }
//          case R.id.subscribe_dlg_cancle_btn => {
//            ""
//          }
//          case _ => {
//            null
//          }
//        }
////        val intent = new Intent(null,)
//      }
//    }
//  }
//}
