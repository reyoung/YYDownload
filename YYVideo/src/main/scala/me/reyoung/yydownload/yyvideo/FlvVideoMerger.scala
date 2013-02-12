package me.reyoung.yydownload.yyvideo

import java.io.{RandomAccessFile, FileInputStream, FileOutputStream, File}
import java.nio.ByteBuffer
import collection.mutable
import collection.mutable.ArrayBuffer

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/8/13
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
object FlvTag {
  val AUDIO_PACKAGE=0x08
  val VIDEO_PACKAGE=0x09
  val METADATA_PACKAGE=0x12

  def readTag(f_in:RandomAccessFile) = {
    val tag = f_in.readByte()
    val szArray  = new Array[Byte](3)
    f_in.read(szArray)
    val sz = ByteBuffer.wrap(Array[Byte](0,szArray(0),szArray(1),szArray(2))).asIntBuffer().get()
//    println(szArray(1))
    val rest = new Array[Byte](sz+8+3)
    f_in.read(rest)
    val buf = new ArrayBuffer[Byte]()
    buf.append(tag)
    buf.append(szArray:_*)
    buf.append(rest:_*)
//    println(rest.length)
    new FlvTag(buf.toArray)
  }
}

class FlvTag(val Buffer:Array[Byte]) {
  final def isAudio() = Buffer(0)==FlvTag.AUDIO_PACKAGE
  final def isVideo() = Buffer(0)==FlvTag.VIDEO_PACKAGE
  final def isMetaData() = Buffer(0)==FlvTag.METADATA_PACKAGE
  final def typeFlags() = Buffer(0)
  final def getBodyLength():Int = {
    ByteBuffer.wrap(Array[Byte](0,Buffer(1),Buffer(2),Buffer(3))).asIntBuffer().get()
  }
  final def getTimestamp():Int = {
    ByteBuffer.wrap(Array[Byte](Buffer(7),Buffer(4),Buffer(5),Buffer(6))).asIntBuffer().get()
  }
  final def getPrevTagSize():Int = {
    ByteBuffer.wrap(Buffer.view(Buffer.length-4,Buffer.length).toArray).asIntBuffer().get()
  }
  final def asMetaData = new FlvMetaTag(this)
}

class FlvMetaTag(val tag:FlvTag) extends FlvTag(tag.Buffer){
  assert(tag.isMetaData())


}

object FlvVideoMerger extends IVideoMerger{
  def merge(output: File, callback: (MergeStatus) => Boolean, videoFiles: File*) = {
    println("Flv Video Merger")
    val f_out = this.openOutputFile(output,callback)
    if (f_out!=null){
      var header:FlvHeader =null
      val inputs =  for (flv <- videoFiles)
      yield this.openInputFile(flv,callback)

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

      for (f_in <- inputs){
        val tag = FlvTag.readTag(f_in)
        println(f_in.readByte())
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
