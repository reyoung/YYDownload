package me.reyoung.yydownload.yyvideo

import java.io.File

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/8/13
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class MergeStatus(val Status:Int){
  final def isOk() = Status>=0
  def getStatusStr()={
    Status match {
      case 0 => "OK"
      case _ => "Unknown Error"
    }
  }
}

trait ParamStatus {
  var Param:Any=null
}

object OpenOutputFileStatus{
  val status_map = Map(
    (-1, "it is directory"),
    (-2, "cannot create output file"),
    (1,"create new output file")
  )
}
case class OpenOutputFileStatus(
  val Output:File,
  override val Status:Int
                           ) extends MergeStatus(Status) {
  override def getStatusStr()={
    val opt = OpenOutputFileStatus.status_map.get(Status)
    if(opt.isDefined){
      opt.get
    } else {
      super.getStatusStr()
    }
  }
}

case class OpenInputFileStatus(
  val Input:File,
  override val Status:Int
                                ) extends MergeStatus(Status){
  override def getStatusStr()
    = Status match {
    case -1 => "Input is directory"
    case -2 => "Input is not exist"
    case _ => super.getStatusStr()
  }
}

case class InputVideoStatus(override val Status:Int) extends MergeStatus(Status)

trait IVideoMerger {
  def merge(output:File, callback:(MergeStatus)=>Unit, videoFiles:File*):Boolean
}
