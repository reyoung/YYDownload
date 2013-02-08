package me.reyoung.yydownload.yyvideo

import java.io.{FileOutputStream, File}

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/8/13
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
object FlvVideoMerger extends IVideoMerger{
  def merge(output: File, callback: (MergeStatus) => Boolean, videoFiles: File*) = {
    println("Flv Video Merger")
    val f_out = this.openOutputFile(output,callback)
    if (f_out!=null){
      f_out.close()
      true
    } else {
      false
    }
  }


  private def openOutputFile(output:File,callback:(MergeStatus)=>Boolean) = {
    if (output.isDirectory){
      callback(OpenOutputFileStatus(output,-1))
      null
    } else {
      val status_code =
      if (!output.exists()){
        if (!output.createNewFile()){
          -2
        } else {
          1
        }
      } else {
        0
      }
      if (status_code<0){
        callback(OpenOutputFileStatus(output,status_code))
        null
      } else {
        val f_out = try {
          new FileOutputStream(output,false)
        } catch {
          case _=> {
            callback(OpenOutputFileStatus(output,-3))
            null
          }
        }
        callback(OpenOutputFileStatus(output,status_code))
        f_out
      }
    }
  }

}
