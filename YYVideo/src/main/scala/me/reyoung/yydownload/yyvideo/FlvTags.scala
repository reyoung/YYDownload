package me.reyoung.yydownload.yyvideo

import java.io.{OutputStream, ByteArrayOutputStream, EOFException, RandomAccessFile}
import java.nio.{ShortBuffer, ByteBuffer}
import collection.mutable.{ListBuffer, ArrayBuffer}

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

  final def DataContent() = Buffer.view(8+3,Buffer.length-7)
  final def asMetaData = new FlvMetaTag(this)
}

object FlvMetaTag{
  val STRING_MARKER:Byte=0x02
  val ECMA_MARKER:Byte=0x08
  val BOOLEAN_MARKER:Byte=0x01
  val DOUBLE_MARKER:Byte=0x00
}

class FlvMetaTag(val tag:FlvTag) extends FlvTag(tag.Buffer){
  assert(tag.isMetaData())

  class ECMAArray(){
    val Data = new ListBuffer[(String,Any)]()
    override def toString() = Data.toString()
  }

  class AMFObject(){
    val Data = new ListBuffer[(String,Any)]()
    override def toString():String = {
      val sb = new StringBuffer()
      this.Data.foreach( kv=>{
        sb.append("##%s:%s\n".format(kv._1,kv._2))
      })
      sb.toString
    }
  }


  private val amfs = new Iterator[Any]{
    var Pos = 0
    val data = DataContent()
    def hasNext: Boolean = Pos < data.length

    def getString = {
//      Pos += 1
      val len = ByteBuffer.wrap(Array[Byte](0,0,data(Pos),data(Pos+1))).asIntBuffer().get()
      Pos += 2
      val sb = new StringBuffer()
      data.view(Pos,Pos+len).foreach( ch=>sb.append(ch.toChar) )
      Pos+= len
      sb.toString
    }

    def getInt32 = {
      val retv = ByteBuffer.wrap(Array[Byte](data(Pos),data(Pos+1),data(Pos+2),data(Pos+3))).asIntBuffer().get()
      Pos += 4
      retv
    }

    def getBoolean = {
      val retv = data(Pos)!=0
      Pos+=1
      retv
    }

    def getECMAArray = {
      val count = getInt32
      val retv = new ECMAArray()
      for (i <- 0 until count){
        val k = getString
        val v = getAMF()
        retv.Data.+=((k,v))
      }
      retv
    }

    def getDouble = {
      val doubleBuf = data.view(Pos,Pos+8)
      Pos += 8
      ByteBuffer.wrap(doubleBuf.toArray).asDoubleBuffer().get()
    }

    def getObject = {
      val retv = new AMFObject()
      val it = new Iterator[(String,Any)]{
        var nextVar = (getString,getAMF())
        def hasNext: Boolean = nextVar._2 != None

        def next(): (String, Any) = {
          val t = nextVar
          nextVar = (getString,getAMF())
          t
        }
      }
      for( kv <- it){
        retv.Data += kv
      }
      retv
    }

    def getStrictArray = {
      val arrayCount = getInt32
      val retv = new ListBuffer[Any]
      for(i <- 0 until arrayCount){
        retv.append(getAMF())
      }
      retv
    }

    def getAMF():Any = {
      data(Pos) match {
        case FlvMetaTag.DOUBLE_MARKER => {
          /**
           * Parse 64bit double
           */
          Pos += 1
          getDouble
        }
        case FlvMetaTag.BOOLEAN_MARKER => {
          /**
           * Parse boolean
           */
          Pos += 1
          getBoolean
        }
        case FlvMetaTag.STRING_MARKER => {
          /**
           * Parse String
           */
          Pos += 1
          this.getString
        }
        case FlvMetaTag.ECMA_MARKER => {
          /**
           * Parse ECMA Array
           */
          Pos += 1
          this.getECMAArray
        }
        case 0x03 => {
          /**
           * Parse Object
           */
          Pos+=1
          this.getObject
        }
        case 0x0a => {
          /**
           * Parse Strict Array
           */
          Pos+=1
          getStrictArray
        }
        case 0x09 =>{
          Pos+=1
          None
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
  }

  def merge(m:FlvMetaTag){
    for (obj <- m.AMFS){
      obj match {
        case str:String => {
          /**
           * Do nothing
           */
        }
        case _ => {
          if(obj.isInstanceOf[ECMAArray]){
            mergeECMAArray(obj)
          } else {
            println(obj.getClass.getName)
            System.exit(2)
          }
        }
      }
    }
  }

  private def mergeECMAArray(obj: Any) {
    val array2 = obj.asInstanceOf[ECMAArray]
    val obj1 = this.AMFS.find(any => any.isInstanceOf[ECMAArray])
    if (obj1.isDefined) {
      val array1 = obj1.get.asInstanceOf[ECMAArray]
      var oldDuration:Double = 0.0
      array2.Data.foreach(kv=>{
        kv._1 match {
          case "metadatacreator"|"width"|"height"|"framerate"|"hasKeyframes"|"hasVideo"|"hasAudio"
               |"hasMetadata"|"audiosamplerate"=>{
            /**
             * Do Nothing
             */
          }
          case "duration" => {
            val index = array1.Data.findIndexOf(kv => kv._1 == "duration")
            oldDuration = array1.Data(index)._2.asInstanceOf[Double]
            val newDuration = kv._2.asInstanceOf[Double]+oldDuration
            array1.Data.update(index,("duration",newDuration))
          }
          case "keyframes" => {

          }
          case _ =>{
            println(kv._1+":"+kv._2)
            System.exit(1)
          }
        }
      })
      array1.Data.update(array1.Data.findIndexOf(kv => kv._1 =="hasKeyframes"),
        ("hasKeyframes",false)
      )
      val index = array1.Data.findIndexOf(kv => kv._1 == "keyframes")
      if (index != -1){
        array1.Data.remove(index,1)
      }
      this.AMFS.update(this.AMFS.findIndexOf(any => any.isInstanceOf[ECMAArray]),array1)
    }
  }

  private def writeStringWithoutTag(s_out:OutputStream,str:String){
    val len = str.length.toShort
    writeShort(s_out, len)
    val str_bytes = str.getBytes
    s_out.write(str_bytes)
  }


  private def writeString(s_out:OutputStream,str:String){
    s_out.write(Array(FlvMetaTag.STRING_MARKER))
    this.writeStringWithoutTag(s_out,str)
  }



  private def writeShort(s_out: OutputStream, len: Short) {
    val bytes = new Array[Byte](2)
    ByteBuffer.wrap(bytes).putShort(len)
    s_out.write(bytes)
  }

  private def writeInt(s_out:OutputStream, i:Int){
    val bytes = new Array[Byte](4)
    ByteBuffer.wrap(bytes).putInt(i)
    s_out.write(bytes)
  }

  private def writeECMAArray(s_out:OutputStream,array:ECMAArray){
    s_out.write(Array(FlvMetaTag.ECMA_MARKER))
    writeInt(s_out,array.Data.length)
    for(dat <- array.Data){
      writeStringWithoutTag(s_out,dat._1)
      this.writeObject(s_out,dat._2)
    }
  }

  def write(file:RandomAccessFile){
    val b_out = new ByteArrayOutputStream()
    for (obj<-AMFS){
      writeObject(b_out, obj)
    }
    b_out.write(Array[Byte](0x00,0x00,0x09)) //! End Of Meta

    file.write(FlvTag.METADATA_PACKAGE)
    val outsz = b_out.size()
    val bytes = new Array[Byte](4)
    ByteBuffer.wrap(bytes).putInt(outsz)
    file.write(bytes,1,3)
    ByteBuffer.wrap(bytes).putInt(getTimestamp())
    file.write(Array[Byte](bytes(3),bytes(0),bytes(1),bytes(2),0x00,0x00,0x00))
    file.write(b_out.toByteArray)
    ByteBuffer.wrap(bytes).putInt(outsz+11)
    file.write(bytes)
  }


  private def writeBoolean(stream: OutputStream, b: Boolean){
    stream.write(Array[Byte](FlvMetaTag.BOOLEAN_MARKER, if(b) 0x01 else 0x00))
  }

  private def writeDoubleWithoutTag(stream:OutputStream,d:Double){
    val bytes = new Array[Byte](8)
    ByteBuffer.wrap(bytes).putDouble(d)
    stream.write(bytes)
  }

  private   def writeDouble(stream: OutputStream, d: Double){
    stream.write(Array[Byte](FlvMetaTag.DOUBLE_MARKER))
    this.writeDoubleWithoutTag(stream,d)
  }

  private def writeObject(b_out: OutputStream, obj: Any) {
    if (obj.isInstanceOf[String]) {
      val str = obj.asInstanceOf[String]

      /**
       * Write String
       */
      this.writeString(b_out, str)
    } else if (obj.isInstanceOf[ECMAArray]) {
      val ecma = obj.asInstanceOf[ECMAArray]

      /**
       * Write Ecma
       */
      this.writeECMAArray(b_out, ecma)
    } else if (obj.isInstanceOf[Boolean]){
      /**
       * Write Boolean
       */
      this writeBoolean(b_out, obj.asInstanceOf[Boolean])
    } else if (obj.isInstanceOf[Double]){
      /**
       * Write Double
       */
      this writeDouble(b_out, obj.asInstanceOf[Double])
    } else {
      println(obj.getClass.getSimpleName)
    }
  }
}