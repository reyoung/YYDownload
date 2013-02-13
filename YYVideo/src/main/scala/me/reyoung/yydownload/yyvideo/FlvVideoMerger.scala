package me.reyoung.yydownload.yyvideo

import java.io._
import scala.Some
import scala.Tuple2
import java.nio.ByteBuffer

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/8/13
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
object FlvVideoMerger extends IVideoMerger{

  var meta:FlvMetaTag = null


  def mergeMeta(m:FlvMetaTag){
    if (meta==null)
      meta = m
    else
      meta.merge(m)
  }

  val yita = 0.2
  var file_audio_base_timestamp = 0
  var audio_frame_avg = 0
  var prev_audio_timestamp = 0

  var file_video_base_timestamp = 0
  var video_frame_avg = 0
  var prev_video_timestamp = 0

  def writeTagReplaceTimeStamp(file:RandomAccessFile,tag:FlvTag,newTimeStamp:Int){
    val bytes = new Array[Byte](4)
    ByteBuffer.wrap(bytes).putInt(newTimeStamp)
//    ByteBuffer.wrap(Array[Byte](Buffer(7),Buffer(4),Buffer(5),Buffer(6))).asIntBuffer().get()
    tag.Buffer.update(7,bytes(0))
    tag.Buffer.update(4,bytes(1))
    tag.Buffer.update(5,bytes(2))
    tag.Buffer.update(6,bytes(3))
    file.write(tag.Buffer)
  }


  def writeAudioTag(file:RandomAccessFile,tag:FlvTag){

    if (tag.getTimestamp() + file_audio_base_timestamp < prev_audio_timestamp){ //! New File
      file_audio_base_timestamp = prev_audio_timestamp + audio_frame_avg
    }

    val curTimeStamp = tag.getTimestamp() + file_audio_base_timestamp
    audio_frame_avg = (yita*audio_frame_avg + (1-yita)*(curTimeStamp - prev_audio_timestamp)).toInt
    prev_audio_timestamp = curTimeStamp
    this.writeTagReplaceTimeStamp(file,tag,curTimeStamp)
  }

  def writeVideoTag(file:RandomAccessFile,tag:FlvTag){
    if (tag.getTimestamp()+file_video_base_timestamp < prev_video_timestamp){
      file_video_base_timestamp = prev_video_timestamp + video_frame_avg
    }
    val curTimeStamp = tag.getTimestamp() + file_video_base_timestamp
    video_frame_avg = (yita*video_frame_avg + (1-yita)*(curTimeStamp-prev_video_timestamp)).toInt
    prev_video_timestamp = curTimeStamp
    this.writeTagReplaceTimeStamp(file,tag,curTimeStamp)
  }


  def merge(output: File, callback: (MergeStatus) => Boolean, videoFiles: File*) = {
    val f_out = this.openOutputFile(output,callback)
    if (f_out!=null){
      var header:FlvHeader =null
      val inputs =  for (flv <- videoFiles) yield this.openInputFile(flv,callback)
      for (f_in <- inputs){
        val head = this.getFLVHeader(f_in,callback)
        if(head.isDefined){
          if (header == null){
            header = head.get
          }
          else {
            if(header!=head.get){
              callback(new FLVCheckHeaderStatus(f_in,-4))
            }
          }
        }
      }

      this.writeFLVHeader(f_out,header,callback)

      val tagIts = new Array[Iterator[FlvTag]](inputs.length)
      var index = 0
      for (f_in <- inputs){
        val tags:Iterator[FlvTag] = new Iterator[FlvTag]{
          val file = f_in
          var nextflvTag = FlvTag.readTag(file)

          def hasNext: Boolean = nextflvTag.isDefined

          def next(): FlvTag = {
            val n = nextflvTag.get
            nextflvTag = FlvTag.readTag(file)
            n
          }
        }
        tagIts(index) = tags
        index += 1
      }

      for (tags <- tagIts){
        val tag = tags.next()
        assert(tag.isMetaData())
        this.mergeMeta(tag.asMetaData)
      }

      /**
       * Write Meta Tag
       */
      this.meta.write(f_out)

      for (tags <- tagIts){
        for (tag <- tags){
          tag.typeFlags() match {
            case FlvTag.AUDIO_PACKAGE =>{
              writeAudioTag(f_out,tag)
            }
            case FlvTag.VIDEO_PACKAGE =>{
              writeVideoTag(f_out,tag)
            }
          }
        }
      }

      for (f_in <- inputs){
        f_in.close()
      }


      f_out.close()
      true
    } else {
      false
    }
  }

  private def writeFLVHeader(f_out:RandomAccessFile,header:FlvHeader,callback:(MergeStatus)=>Boolean)={
    val bytes = Array[Byte](
      'F','L','V',header.Version(),header.Flags(),0,0,0,9,0,0,0,0
    )
    f_out.write(bytes)
  }

  private class FlvHeader(val t1:Byte, val t2:Byte) extends Tuple2[Byte,Byte](t1,t2) {
    def Version()= this._1

    def isSupportVideo()=this._2&0x01

    def isSupportAudio()=this._2&0x04

    def Flags()= this._2
  }
  private case class FLVCheckHeaderStatus( val RandomFile :RandomAccessFile,
                                           override val Status:Int) extends InputVideoStatus(Status){
    override def getStatusStr():String = {
      Status match {
        case -1=> "Signature Check Error"
        case -2=> "Not support flv version"
        case -3=> "Flv header size not match"
        case -4=> "Files header not same"
        case _ => super.getStatusStr()
      }
    }
  }

  private def getFLVHeader(f_in:RandomAccessFile,callback:(MergeStatus)=>Boolean)
  :Option[FlvHeader]= {

    def checkSignature(f_in:RandomAccessFile) = {
      val buf = new Array[Byte](3)
      f_in.read(buf)
      buf(0)=='F' && buf(1)=='L' && buf(2)=='V'
    }
    try {
      f_in.seek(0)
      if(checkSignature(f_in)){
        val version = f_in.readByte()
        if (version==1) {
          val flags = f_in.readByte()
          val headerSz = f_in.readInt()
          if(headerSz!=9){
            callback(new FLVCheckHeaderStatus(f_in,-3))
            None
          } else {
            val buf = new Array[Byte](4)
            f_in.read(buf)
            if(!buf.forall((p)=>p==0)){
              /**
               * @todo Do Some Warn
               */
            }

            Some(new FlvHeader(version,flags))
          }
        } else {
          callback(new FLVCheckHeaderStatus(f_in,-2))
          None
        }
      } else {
        callback(new FLVCheckHeaderStatus(f_in,-1))
        None
      }
    } catch {
      case _ => {
        callback(new FLVCheckHeaderStatus(f_in,-1024))
        None
      }
    }
  }

  private def openInputFile(input:File,callback:(MergeStatus)=>Boolean) = {
    if (input.isDirectory){
      callback(OpenInputFileStatus(input,-1))
      null
    }
    else if (input.exists()) {
      val f_in =
        try {
          new RandomAccessFile(input,"r")
        } catch {
          case _ => null
        }
      if (f_in==null){
        callback(OpenInputFileStatus(input,-3))
      } else {
        callback(OpenInputFileStatus(input,0))
      }
      f_in
    }
    else {
      callback(OpenInputFileStatus(input,-2))
      null
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
          new RandomAccessFile(output,"rw")
        } catch {
          case _:Exception=> {
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
