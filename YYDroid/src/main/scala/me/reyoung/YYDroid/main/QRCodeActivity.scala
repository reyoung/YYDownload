package me.reyoung.YYDroid.main

import me.reyoung.YYDroid.util.{DatabaseUtil, LogTag}
import android.app.Activity
import android.util.Log
import net.sourceforge.zbar.{Image, ImageScanner}
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity
import android.os.Bundle
import android.content.{Context, Intent}
import android.view.{SurfaceHolder, SurfaceView}
import android.hardware.Camera
import me.reyoung.R
import android.widget.FrameLayout
import android.os

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/21/13
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */

class QRCodeCallback(method:(String)=>Unit) extends Function[String,Unit] with Serializable{
  def apply(v1: String) {
    method(v1)
  }
}

object QRCodeActivity extends LogTag {

  private def CallMe(atv:Activity,callback:QRCodeCallback):Unit = {
    Log.d(LogTag,"During CallMe..")

    val intent = new Intent()
    intent.setClass(atv,classOf[QRCodeActivity])
    intent.putExtra("callback",callback)
    atv.startActivity(intent)

    Log.d(LogTag,"Exit CallMe..")
    null
  }
  def CallMe(atv:Activity, callback:(String)=>Unit):Unit = {
    this.CallMe(atv,new QRCodeCallback(callback) )
  }

  def getCameraInstance()= try {
    Camera.open()
  } catch {
    case _ => {null}
  }
}

class QRCodeActivity extends OrmLiteBaseActivity[DatabaseUtil] with LogTag with Camera.PreviewCallback{
  var mCamera:Camera = null
  try {
    System.loadLibrary("iconv")
    Log.d(LogTag,"libiconv loaded")
  } catch {
    case ex:Throwable =>{
      Log.e(LogTag,"Error load iconv",ex)
    }
  }
  var scanner:ImageScanner = null
  var callback:QRCodeCallback = null

  override def onCreate(bundle:Bundle){
    super.onCreate(bundle)
    this.setContentView(R.layout.qr_code_activity)
    callback  = bundle.getSerializable("callback").asInstanceOf[QRCodeCallback]
    Log.d(LogTag,"On QRCodeActivity Created")
    scanner = new ImageScanner
    mCamera = QRCodeActivity.getCameraInstance()
    Log.d(LogTag,"Get Camera Instance "+mCamera)
    val preview = new CameraPreview(this,mCamera,this)
    findViewById(R.id.qr_code_activity_camera_frame).asInstanceOf[FrameLayout]
      .addView(preview)
    Log.d(LogTag,"Finish QRCode Activity On Create")
  }

  def onPreviewFrame(data: Array[Byte], camera: Camera) {
//    Log.d(LogTag,"On Preview Frame")
    val size = camera.getParameters.getPreviewSize
    val img = new Image(size.width,size.height,"Y800")
    img.setData(data)
    val result = scanner.scanImage(img)
//    Log.d(LogTag,"On Scanner Scan "+result);
    if(result!=0){
      Log.d(LogTag,"On QR code found")
      for (symb <- scanner.getResults.toArray){
        val symbol = symb.asInstanceOf[net.sourceforge.zbar.Symbol]
        if (symbol.getType == net.sourceforge.zbar.Symbol.QRCODE){
          Log.d(LogTag,"Founded QR Code "+symbol.getData)
          callback(symbol.getData)
          this.finish()
        } else {
          Log.d(LogTag,"WTF Symbol Type "+symbol.getType)
        }
      }
    }
  }

  override def onPause(){
    super.onPause()
    if (mCamera!=null){
      mCamera.release()
      mCamera = null
    }
  }

  override def onResume(){
    super.onResume()
    mCamera = QRCodeActivity.getCameraInstance()
  }
}

class CameraPreview(mContext:Context,
                     mCamera:Camera,
                     mPreviewCallback:Camera.PreviewCallback
                     ) extends SurfaceView(mContext) with  SurfaceHolder.Callback with LogTag{
  val mHolder = this.getHolder
  mHolder.addCallback(this)
  mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
  def surfaceCreated(holder: SurfaceHolder) {
//    Log.d(LogTag,"Surface Created")
    this.startPreview()
  }

  def surfaceChanged(holder: SurfaceHolder, fmt: Int, w: Int, h: Int) {
//    Log.d(LogTag,"Surface Changed")
    if (mHolder.getSurface!=null&&mCamera!=null){
      try {
        mCamera.stopPreview()
      } catch {
        case _ => {}
      }
      startPreview()
    }
  }


  private def startPreview() {
    mCamera.setPreviewDisplay(mHolder)
    mCamera.setPreviewCallback(mPreviewCallback)
    mCamera.startPreview()
  }

  def surfaceDestroyed(p1: SurfaceHolder) {
//    Log.d(LogTag,"Surface Created")
  }
}