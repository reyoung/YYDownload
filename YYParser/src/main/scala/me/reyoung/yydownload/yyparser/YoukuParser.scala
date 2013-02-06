package me.reyoung.yydownload.yyparser

import java.net.URL
import reflect.BeanProperty
import util.matching.Regex
import xml.NodeSeq
import util.parsing.json.JSON
import collection.mutable.ListBuffer

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 13-2-3
 * Time: 下午3:25
 * EMail: reyoung@126.com
 * Blog: www.reyoung.me
 */
class YoukuParser extends IParser{


  protected class YoukuParserResult(@BeanProperty val Title:String,
                                    val DownloadUrls:List[(URL, Int)]
                                     ) extends IParseResult with AuthorIDTrait with AuthorNameTrait
  {
    val SiteDescription="Youku"
    var AuthorID:Int=0
    var AuthorName: String = null
  }


  override def parse(url: URL, definition: VideoDefinition.Type):IParseResult = {
    val id = this.getVideoId(url)
    if(id!=null){
//      println("ID of the video is "+id)
      val urlStr = "http://v.youku.com/v_show/id_%s.html".format(id)
      val page = this.retirePageDom(urlStr)
      val title = this.getTitleFromPage(page)
      val json_str = this.retirePageString("http://v.youku.com/player/getPlayList/VideoIDS/"+id)
//      println("Json string is ",json_str)
      val result = JSON.parseFull(json_str)
      if(result.isDefined && result.get.isInstanceOf[Map[String,Any]]){
        val rresult = result.get.asInstanceOf[Map[String,Any]]
        val download_urls = this.getDownloadUrlById(rresult,definition)
        val retv = new YoukuParserResult(title,download_urls)
        retv.AuthorID = this.getAuthorID(rresult)
        retv.AuthorName = this.getAuthorName(rresult)
        retv
      } else {
        println(JSON.lastNoSuccess)
        null
      }
    } else {
      null
    }
  }

  protected def getAuthorID(result:Map[String,Any]):Int={
    val retv = result("data").asInstanceOf[List[Any]](0).asInstanceOf[Map[String,Any]]("userid")
    retv.asInstanceOf[String].toInt
  }

  protected def getAuthorName(result:Map[String,Any]):String=
    result("data").asInstanceOf[List[Any]](0).asInstanceOf[Map[String,Any]]("username").asInstanceOf[String]

  @BeanProperty
  override val SiteDescription = "Youku"


  protected def getTitleFromPage(page:NodeSeq):String={
    val meta = page \\ "html" \ "head" \ "meta"
    var retv:String=null
    for(m<-meta if retv==null){
      if( (m \\ "@name").toString == "title"){
        retv = (m \\ "@content").toString()
      }
    }
    if (retv!=null){
      retv = this.trimTitle(retv)
    }
    retv
  }

  protected def trimTitle(title:String)=
    title match {
      case null=> null
      case _ =>{
        var t = title
        val remove_strings = Array(
          "—在线播放—优酷网，视频高清在线观看"
        )
        for (r<-remove_strings){
          t =  t.replace(r,"")
        }
        t
      }
    }

  protected def getDownloadUrlById(result:Map[String,Any],definition: VideoDefinition.Type)={
    val listBuf = new ListBuffer[Tuple2[URL,Int]]()
    val data = result("data")
    assert(data.isInstanceOf[List[Any]]&&data.asInstanceOf[List[Any]].length==1
      &&data.asInstanceOf[List[Any]](0).isInstanceOf[Map[String,Any]])
    val real_data = data.asInstanceOf[List[Any]](0).asInstanceOf[Map[String,Any]]
    assert(real_data("segs").isInstanceOf[Map[String,Any]])
    val segs = real_data("segs").asInstanceOf[Map[String,Any]]
    val key = definition match {
      case VideoDefinition.NORMAL => "flv"
      case VideoDefinition.HIGH => {
        if(segs.keys.toSet.contains("mp4")){
          "mp4"
        } else {
          "flv"
        }
      }
      case VideoDefinition.SUPER =>{
        if(segs.keys.toSet.contains("hd2")){
          "hd2"
        } else if (segs.keys.toSet.contains("mp4")){
          "mp4"
        } else {
          "flv"
        }
      }
    }
    var seed = real_data("seed").asInstanceOf[Double].toInt
    val mixed = new StringBuilder()
    val source = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ/\\:._-1234567890".getBytes.toBuffer
    while(!source.isEmpty){
      seed = (seed*211+30031)&0xFFFF
      val index = seed*source.length >> 16
      val c = source(index)
      source.remove(index)
      mixed.append(c.toChar)
    }
    val ids = real_data("streamfileids").asInstanceOf[Map[String,Any]](key).asInstanceOf[String].split("""\*""")
    val vidSb= new StringBuilder()
    for(id<-ids if !id.isEmpty){
      vidSb.append(mixed(id.toInt))
    }
    val vidlow = vidSb.toString().substring(0,8)
    val vidhigh = vidSb.toString().substring(10,vidSb.length)

    for (s<-segs(key).asInstanceOf[List[Any]]){
      val sm = s.asInstanceOf[Map[String,Any]]

      val no = if(sm("no").isInstanceOf[String]) {
        "%02X".format(sm("no").asInstanceOf[String].toInt)
      } else if(sm("no").isInstanceOf[Double]) {
        "%02X".format(sm("no").asInstanceOf[Double].toInt)
      }
      //        println(no)
      val url = "http://f.youku.com/player/getFlvPath/sid/00_%s/st/%s/fileid/%s%s%s?K=%s".format(
        no,key,vidlow,no,vidhigh,sm("k").asInstanceOf[String]
      )
      listBuf.append((new URL(url),sm("size").asInstanceOf[String].toInt))
    }
    //    }
    listBuf.toList
  }

  protected def getVideoId(url:URL) ={
    val patterns = Array(
      new Regex("""http://player\.youku\.com/player\.php/sid/([A-Za-z0-9]+)/v\.swf""","id"),
      new Regex( """http://(v|www)\.youku\.com/v_show/id_([A-Za-z0-9]+)(|_rss)\.html""","t1","id","t2"),
      new Regex("""^loader\.swf\?VideoIDS=([A-Za-z0-9]+)""","id"),
      new Regex("""^([A-Za-z0-9]+)$""","id")
    )
    val urlStr = url.toString
    var id:String = null
    for(p <- patterns if id==null){
      val mat = p.findFirstMatchIn(urlStr)
      if(mat.isDefined){
        id = mat.get.group("id")
      }
    }
    id
  }
}
