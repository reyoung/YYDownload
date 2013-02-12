package me.reyoung.yydownload.yyvideo

import java.io.{EOFException, RandomAccessFile}
import java.nio.ByteBuffer
import collection.mutable.{ListBuffer, ArrayBuffer}
import collection.mutable

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/12/13
 * Time: 6:41 PM
 * To change this template use File | Settings | File Templates.
 */
object FlvTag {
  val AUDIO_PACKAGE=0x08
  val VIDEO_PACKAGE=0x09
  val METADATA_PACKAGE=0x12

  def readTag(f_in:RandomAccessFile):Option[FlvTag] = {
    val tag = try {
      f_in.readByte()
    } catch {
      case _:EOFException => return None
    }
    val szArray  = new Array[Byte](3)
    if(f_in.read(szArray) == -1){
      return None
    }
    val sz = ByteBuffer.wrap(Array[Byte](0,szArray(0),szArray(1),szArray(2))).asIntBuffer().get()
    val rest = new Array[Byte](sz+8+3)
    if(f_in.read(rest) == -1){
      return None
    }
    val buf = new ArrayBuffer[Byte]()
    buf.append(tag)
    buf.append(szArray:_*)
    buf.append(rest:_*)
    return Some(new FlvTag(buf.toArray))
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

  final def DataContent() = Buffer.view(8+3,Buffer.length-4)
  final def asMetaData = new FlvMetaTag(this)
}

class FlvMetaTag(val tag:FlvTag) extends FlvTag(tag.Buffer){
  assert(tag.isMetaData())
  private val amfs = new Iterator[Any]{
    var Pos = 0
    val data = DataContent()
    def hasNext: Boolean = Pos < data.length

    def getString = {
      Pos += 1
      val len = ByteBuffer.wrap(Array[Byte](0,0,data(Pos),data(Pos+1))).asIntBuffer().get()
      Pos += 2
      val sb = new StringBuffer()
      data.view(Pos,Pos+len).foreach( ch=>sb.append(ch.toChar) )
      Pos+= len
      sb.toString
    }

    def getAMF() = {
      data(Pos) match {
        case 0x00 => {
          /**
           * Parse 64bit double
           */
          Pos += 1
          val doubleBuf = data.view(Pos,Pos+8)
          Pos += 8
          ByteBuffer.wrap(doubleBuf.toArray).asDoubleBuffer().get()
        }
        case 0x02 => {
          /**
           * Parse String
           */
          this.getString
        }
        case 0x0a => {
          /**
           * Parse Object
           */
          System.exit(1)
        }
        case _ => {
          println(Pos+" "+data(Pos).toInt)
          System.exit(1)
        }
      }
    }

    def next(): Any = getAMF()
  }
  val AMFS = new ListBuffer[Any]()
  for (amf <- amfs){
    AMFS += amf
    println(amf)
  }
}