package me.reyoung.yydownload.yyvideomerger

import me.reyoung.yydownload.yyvideomerger.Main.Args
import java.io.File
import me.reyoung.yydownload.yyvideo.{Mp4VideoMerger, MergeStatus, FlvVideoMerger, IVideoMerger}

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/14/13
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
object MainHandler {
  def handle(){
    val extName = this.checkExtNames()
    val merger = this.getMerger(extName)
    merger.merge(new File(Args.OutputFileName),
    process, (for (file <- Args.InputFiles.toArray()) yield {
        new File(file.asInstanceOf[String])
      }):_*
    )
  }
  def checkExtNames():String = {
    val files = Args.InputFiles.toArray()
    val extNameList = for (file <- files) yield {
      val fn = file.asInstanceOf[String]
      fn.substring(fn.lastIndexOf('.')+1).toLowerCase()
    }
    val outExt = Args.OutputFileName.substring(Args.OutputFileName.lastIndexOf('.')+1).toLowerCase()
    if (extNameList.forall( p => p==outExt)){
      outExt
    } else {
      throw new RuntimeException("Input and Output File Extention Must Be Same")
      null
    }
  }
  def getMerger(extName:String):IVideoMerger = extName match {
    case "flv" => {
      new FlvVideoMerger
    }
    case "mp4" => {
      new Mp4VideoMerger
    }
    case _ => {
      throw new RuntimeException("Cannot merge such type video")
      null
    }
  }

  def process(status:MergeStatus){
    if(!status.isOk()){
      println("Error! "+status.getStatusStr())
      System.exit(1)
    }
  }
}
