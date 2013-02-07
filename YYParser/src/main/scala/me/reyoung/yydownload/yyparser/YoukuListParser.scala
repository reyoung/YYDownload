package me.reyoung.yydownload.yyparser

import java.net.URL
import util.matching.Regex
import xml.NodeSeq
import collection.immutable.IndexedSeq
import collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/7/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */

class YoukuListParseResult extends IListParseResult with AuthorNameTrait{
  var Title:String =null

  def Videos(): List[IParseResult] = mVideoBuf.toList

  var AuthorName: String = null

  val mVideoBuf = new ListBuffer[IParseResult]()
}




object YoukuListParser extends IListParser {
  val SiteDescription: String = "Youku"

  def parse(url: URL,vd:VideoDefinition.Type, process:(Int,Int)=>Unit ): IListParseResult = {
    val id = YoukuListParserHelper.getListIdFromUrl(url)
    id match {
      case 0 => null
      case _ =>{
        val retv = new YoukuListParseResult
        val listDom = YoukuListParserHelper.getVideoListPageDom(id)
        retv.Title = YoukuListParserHelper.getTitleByDom(listDom)
        retv.AuthorName = YoukuListParserHelper.getAuthorByDom(listDom)
        val count = YoukuListParserHelper.getVideoListNumber(listDom)
        val d_urls = YoukuListParserHelper.getVideoURLs(id,count,process)
        val youku = YoukuParser
        for (url <- d_urls){
          val result = youku.parse("http://v.youku.com/v_show/id_%s.html".format(url),vd)
          retv.mVideoBuf += result
        }
        retv
      }
    }
  }


}

private object YoukuListParserHelper extends HttpUtil{
  def getListIdFromUrl(url:String):Long={
    val Regexps = Array(
      new Regex("""http://www\.youku\.com/playlist_show/id_(\d+)(_.*|)\.html""",
      "id","postfix"),
      new Regex("""http://(v|www)\.youku\.com/v_show/id_(.*)\.html?(.*)f=(\d+)""",
      "www","vid","params","id"
      ),
      new Regex("""http://v\.youku\.com/v_playlist/f(\d+)o[01]p(\d+)\.html""",
      "id","num")
    )
    var retv:Long = 0
    for (r<-Regexps if retv == 0){
      val m = r.findFirstMatchIn(url)
      if(m.isDefined){
        retv = m.get.group("id").toLong
      }
    }
    retv
  }

  def getVideoListPageDom(id:Long):NodeSeq =
    this.retirePageDom("http://www.youku.com/playlist_show/id_%d.html".format(id))


  def getTitleByDom(dom:NodeSeq) = {
    val meta = dom \\ "html" \ "head" \ "meta"
    var retv:String=null
    for(m<-meta if retv==null){
      if( (m \\ "@name").toString == "title"){
        retv = (m \\ "@content").toString()
      }
    }
    this.trim_title(retv)
  }

  def getTitleById(id:Long):String =
    this.getTitleByDom(this.getVideoListPageDom(id))


  def trim_title(title:String) = {
    title match {
      case null => null
      case _ => {
        var retv = title
        val tArray = Array(" - 专辑 - 优酷视频")
        for ( str <- tArray){
          retv = retv.replace(str,"")
        }
        retv
      }
    }
  }

  def getAuthorByDom(dom:NodeSeq):String = {
    val a = ((((((((dom\\"html"\"body"\"div")
      .filter( n => {
      (n\"@class").toString == "s_main col2_21"
    })\"div" ).filter( n => (n\"@class").toString=="right")
      \"div").filter(n=>(n\"@class").toString()=="listInfo")
      \"div").filter(n=>(n\"@class").toString()=="box nBox")
      \"div").filter(n=>(n\"@class").toString()=="body")
      \"div").filter(n=>(n\"@class").toString()=="offical")
      \"div").filter(n=>(n\"@class").toString()=="name")
      \"a").filter(n=>{
      val href = (n\"@href").toString()
      href.startsWith("http://i.youku.com/")
    })
    a.text
  }

  def getVideoListNumber(dom:NodeSeq):Int = {
    val number = (dom \\"span").filter(n => {
      (n\"@class").toString() == "num" && !n.text.exists(ch => !ch.isDigit)
    }).filter(n => {
      (n\"@id").length==0
    }).head
    number.text.toInt
  }

  def getVideoURLs(id:Long,len:Int,process:(Int,Int)=>Unit):Stream[String] =
    for (i <- (0 until len).toStream ) yield {
      process(i+1,len)
      val v_url = "http://v.youku.com/v_playlist/f%do0p%d.html".format(id,i)
      val page = retirePageString(v_url)
      val idRegex = new Regex("var\\s+videoId2\\s*=\\s*'(\\S+)'","vid")
      val m = idRegex.findFirstMatchIn(page)
      if(m.isDefined) {
        val retv = m.get.group("vid")
        retv
      } else {
        ""
      }
    }


}