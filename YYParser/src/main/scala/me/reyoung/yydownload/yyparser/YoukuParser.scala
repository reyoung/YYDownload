package me.reyoung.yydownload.yyparser

import java.net.URL
import reflect.BeanProperty
import util.matching.Regex
import xml.{NodeSeq, Xhtml, XML}
import nu.validator.htmlparser.sax.HtmlParser
import io.Source
import java.io.StringReader
import org.xml.sax.{Attributes, Locator, ContentHandler, InputSource}
import xml.parsing.NoBindingFactoryAdapter
import javax.xml.parsers.SAXParser
import util.parsing.json.JSON
import util.Random
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
                                    val DownloadUrls:List[Tuple2[URL,Int]]
                                     ) extends IParseResult with AuthorIDTrait
  {
    var AuthorID:Int=0
  }


  override def parse(url: URL, definition: VideoDefinition.Type):IParseResult = {
    val id = this.getVideoId(url)
    if(id!=null){
      val urlStr = "http://v.youku.com/v_show/id_%s.html".format(id)
      val page = this.retirePageDom(urlStr)
      val title = this.getTitleFromPage(page)
      val json_str = this.retirePageString("http://v.youku.com/player/getPlayList/VideoIDS/"+id)
      val result = JSON.parseFull(json_str)
      if(result.isDefined && result.get.isInstanceOf[Map[String,Any]]){
        val rresult = result.get.asInstanceOf[Map[String,Any]]
        val download_urls = this.getDownloadUrlById(rresult,definition)
        val retv = new YoukuParserResult(title,download_urls)
        retv.AuthorID = this.getAuthorID()
        retv
      } else {
        null
      }
    } else {
      null
    }
  }

  protected def getAuthorID():Int={
    0
  }
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
    //    val json_str = this.retirePageString("http://v.youku.com/player/getPlayList/VideoIDS/"+id)
    ////    val json_str = """{"data":[{"ct":"g","cs":"2218","logo":"http:\/\/g4.ykimg.com\/1100641F465103E84F26AB01B4746A001CD2DB-C4F9-E859-638D-7869A29CAFFA","seed":9702,"tags":["xiaoY","6v6","\u9b54\u517d\u4e89\u9738","\u9b54\u517d\u89e3\u8bf4"],"categories":"99","videoid":"126815022","vidEncoded":"XNTA3MjYwMDg4","username":"\u90aa\u6076\u7684\u5c0f\u8c03","userid":"28603498","title":"\u3010\u5927\u62db\u7684\u9006\u88ad\u3011\u9b54\u517d\u4e89\u9738xiaoy\u89e3\u8bf46v6 EI12\u4eba","up":0,"down":0,"ts":"MIRO*jAlkCkyG6AQAcJ4tgE","tsup":"MIRIMDMlkCkyG6AQAgh*tgE","key1":"bd72ff97","key2":"98dabfade414aadb","tt":"0","videoSource":"10020","seconds":"2320.53","streamfileids":{"hd2":"4*26*4*4*4*27*4*14*4*4*16*27*4*26*66*34*55*3*38*57*34*4*4*27*51*55*19*55*14*38*3*66*3*4*14*19*66*19*20*26*14*52*16*20*56*44*66*34*20*38*55*3*34*20*52*19*56*19*66*44*16*51*55*16*26*19*","mp4":"4*26*4*4*4*34*4*19*4*4*16*27*4*26*56*16*56*34*38*57*34*4*4*27*51*55*19*55*14*38*3*66*3*4*14*19*66*19*20*26*14*52*16*20*56*44*66*34*20*38*55*3*34*20*52*19*56*19*66*44*16*51*55*16*26*19*","flv":"4*26*4*4*4*44*4*14*4*4*16*27*4*26*51*57*38*52*38*57*34*4*4*27*51*55*19*55*14*38*3*66*3*4*14*19*66*19*20*26*14*52*16*20*56*44*66*34*20*38*55*3*34*20*52*19*56*19*66*44*16*51*55*16*26*19*"},"segs":{"hd2":[{"no":"0","size":"42093160","seconds":"379","k":"33d02eae6b93e17524114b3a","k2":"18ac6808c4596385e"},{"no":"1","size":"71864122","seconds":"417","k":"34c77232a11c4f6c28285a32","k2":"111cbc7b57ff6140b"},{"no":"2","size":"66321105","seconds":"370","k":"5f7fc20f1d11f70e24114b3a","k2":"1eea5f44bbd55da10"},{"no":"3","size":"72023935","seconds":"408","k":"78f60dc882da335b24114b3a","k2":"1c84f832409d635d0"},{"no":"4","size":"73046823","seconds":"396","k":"b7fd7bc1ebd231e128285a32","k2":"161cd8f3c743c2724"},{"no":"5","size":"22943964","seconds":"349","k":"f6a87a1d98d7fef724114b3a","k2":"1b9a49db5c3174321"}],"mp4":[{"no":"0","size":"18442139","seconds":"381","k":"0c82f352922a393f28285a32","k2":"1b14edaa61102172a"},{"no":"1","size":"28246912","seconds":"364","k":"8954a482cbc446f924114b3a","k2":"1d68083eb167ca5a7"},{"no":"2","size":"32726373","seconds":"397","k":"b6368d46195e025828285a32","k2":"118c250ad7cd720a1"},{"no":"3","size":"30063633","seconds":"376","k":"4e3f33567d5c6b8b24114b3a","k2":"1ca7a62d91348e072"},{"no":"4","size":"33467624","seconds":"362","k":"05c29d230fd66e1224114b3a","k2":"16b572f2f62c415e5"},{"no":"5","size":"9998847","seconds":"205","k":"287304eecb8c07f8261cd2b6","k2":"1d072595a1358203c"},{"no":"6","size":"6618327","seconds":"236","k":"02043164cb84237028285a32","k2":"182c1506e77ce6806"}],"flv":[{"no":"0","size":"10415562","seconds":"393","k":"ea1250a2c860f8f6261cd2b6","k2":"10f28684b1b580af5"},{"no":"1","size":"15816002","seconds":"391","k":"a8d5a50e0166a75c28285a32","k2":"1a13edc3e93b2de2a"},{"no":"2","size":"16814646","seconds":"394","k":"15998ddd73254ee728285a32","k2":"1510057eb169230a4"},{"no":"3","size":"15630739","seconds":"371","k":"6c5753f09706e7bb28285a32","k2":"1979b8c1d067a346e"},{"no":"4","size":"18257316","seconds":"413","k":"733dd9164c0cad6624114b3a","k2":"1dacbf5a9096b3b05"},{"no":"5","size":"6340617","seconds":"358","k":"b6729e152aeac75a28285a32","k2":"14f6435194fc9fd78"}]},"streamsizes":{"hd2":"348293109","mp4":"159563855","flv":"83274882"},"stream_ids":{"hd2":"110520200","mp4":"110504584","flv":"110487465"},"streamlogos":{"hd2":0,"mp4":0,"flv":0},"streamtypes":["hd2","mp4","flv"],"streamtypes_o":["hd2","flvhd","mp4"]}],"user":{"id":0},"controller":{"search_count":true,"mp4_restrict":1,"stream_mode":1,"video_capture":true,"area_code":120000,"dma_code":4837,"continuous":0,"playmode":"normal","circle":false,"tsflag":false,"share_disabled":false,"download_disabled":false,"pc_disabled":false,"pad_disabled":false,"mobile_disabled":false,"tv_disabled":false}}"""
    //    val result = JSON.parseFull(json_str)
    val listBuf = new ListBuffer[Tuple2[URL,Int]]()
    //    if(result.isDefined && result.get.isInstanceOf[Map[String,Any]]){
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
      val no = "%02X".format(sm("no").asInstanceOf[String].toInt)
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
