package me.reyoung.yydownload.yyparser

import reflect.BeanProperty
import java.net.URL
import java.net.HttpURLConnection
import xml.NodeSeq
import xml.parsing.NoBindingFactoryAdapter
import org.xml.sax.InputSource
import javax.xml.parsers.SAXParser
import java.io.InputStreamReader

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 13-2-3
 * Time: 下午3:10
 * EMail: reyoung@126.com
 * Blog: www.reyoung.me
 */
class HTML5Parser extends NoBindingFactoryAdapter{
  override def loadXML(source : InputSource, _p: SAXParser) = {
    loadXML(source)
  }

  def loadXML(source : InputSource) = {
    import nu.validator.htmlparser.{sax,common}
    import sax.HtmlParser
    import common.XmlViolationPolicy

    val reader = new HtmlParser
    reader.setXmlPolicy(XmlViolationPolicy.ALLOW)
    reader.setContentHandler(this)
    reader.parse(source)
    rootElem
  }

}

object VideoDefinition extends Enumeration{
  type Type = Value
  val NORMAL = Value(0,"NORMAL")
  val HIGH  = Value(1,"HIGH")
  val SUPER = Value(2,"SUPER")
}

trait AuthorIDTrait{
  def AuthorID():Int
}
trait AuthorNameTrait{
  def AuthorName():String
}

trait HttpUtil {
  implicit def url2str(url:URL) = url.toString


  final protected def retirePageDom(url:URL):NodeSeq={
    var conn:HttpURLConnection=null
    var msg:NodeSeq = null
    try{
      conn = url.openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("GET")
      val code = conn.getResponseCode
      if (code>=200 && code <400) {
        val is = conn.getInputStream
        val html5Parser = new HTML5Parser
        msg = html5Parser.loadXML(new InputSource(is))
      }
    }finally{
      if(conn!=null)
        conn.disconnect()
    }
    msg
  }
  final protected def retirePageDom(url:String):NodeSeq = this.retirePageDom(new URL(url))
  final protected def retirePageString(url:URL):String = {
    var conn:HttpURLConnection =null
    var msg:String = null
    try{
      conn = url.openConnection().asInstanceOf[HttpURLConnection]
      conn.setRequestMethod("GET")
      val sb = new StringBuffer()
      val status = conn.getResponseCode
      if(status>=200&&status<400){
        val in = conn.getInputStream
        val reader = new InputStreamReader(in)
        val buff = new Array[Char](1024)
        var sz:Int = 0
        do {
          sz = reader.read(buff)
          if (sz != -1)
            sb.append(buff,0,sz)
        } while(sz != -1)
        msg = sb.toString
      }
    } finally{
      if(conn!=null)
        conn.disconnect()
    }
    msg
  }
  final protected def retirePageString(url:String):String = this.retirePageString(new URL(url))
}

trait IParseResult{
  @BeanProperty
  val Title:String

  def DownloadUrls():List[(URL, Int)]

  final def getAuthorId = this.asInstanceOf[AuthorIDTrait].AuthorID
  final def getAuthorName = this.asInstanceOf[AuthorNameTrait].AuthorName

  val SiteDescription:String

  def FileExtName() :String
}

trait IParser extends HttpUtil{

  @BeanProperty
  val  SiteDescription:String

  def parse(url:URL,definition:VideoDefinition.Type):IParseResult

  final def parse(url:String,definition:VideoDefinition.Type):IParseResult = this.parse(new URL(url),definition)

}


