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
object YoukuParser extends IParser{


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
//      val json_str = "{\"data\":[{\"ct\":\"g\",\"cs\":\"2218\",\"logo\":\"http:\\/\\/g4.ykimg.com\\/1100641F46511202A3D4F901B4746A784D0FCA-9B40-49DC-01F0-5A97D737CF9F\",\"seed\":9592,\"tags\":[\"xiaoY\",\"\\u7389\\u7c73\",\"th000\",\"\\u9b54\\u517d\\u4e89\\u9738\",\"\\u9b54\\u517d\\u89e3\\u8bf4\"],\"categories\":\"99\",\"videoid\":\"127932256\",\"vidEncoded\":\"XNTExNzI5MDI0\",\"username\":\"\\u90aa\\u6076\\u7684\\u5c0f\\u8c03\",\"userid\":\"28603498\",\"title\":\"\\u3010\\u7b2c\\u4e00\\u6b21\\u3011\\u9b54\\u517d\\u4e89\\u9738xiaoy\\u89e3\\u8bf4\\u9aa8\\u7070\\u676f\\u7389\\u7c73 vs th000 TM\",\"up\":0,\"down\":0,\"ts\":\"MKo3GDIgciw2GUASASEAmAE\",\"tsup\":\"MKo-PzEgciw2GUASAgYImAE\",\"key1\":\"bd72fea4\",\"key2\":\"362efdbcf001f4e2\",\"tt\":\"0\",\"videoSource\":\"10020\",\"seconds\":\"1076.43\",\"streamfileids\":{\"hd2\":\"43*32*43*43*43*14*43*47*43*43*64*14*14*24*43*24*15*43*15*7*58*57*43*14*11*58*49*58*47*15*14*11*32*64*15*60*14*55*26*14*47*57*43*26*32*7*64*64*26*15*15*7*21*26*7*55*60*24*60*7*43*58*11*43*7*21*\",\"mp4\":\"43*32*43*43*43*21*43*32*43*43*64*14*14*14*55*11*32*47*15*7*58*57*43*14*11*58*49*58*47*15*14*11*32*64*15*60*14*55*26*14*47*57*43*26*32*7*64*64*26*15*15*7*21*26*7*55*60*24*60*7*43*58*11*43*7*21*\",\"flv\":\"43*32*43*43*43*24*43*32*43*43*64*14*14*14*55*43*24*15*15*7*58*57*43*14*11*58*49*58*47*15*14*11*32*64*15*60*14*55*26*14*47*57*43*26*32*7*64*64*26*15*15*7*21*26*7*55*60*24*60*7*43*58*11*43*7*21*\"},\"segs\":{\"hd2\":[{\"no\":\"0\",\"size\":\"34596521\",\"seconds\":\"188\",\"k\":\"f91735eb8dc83ebb24114b92\",\"k2\":\"1902b26082cd8a9ca\"},{\"no\":\"1\",\"size\":\"23934096\",\"seconds\":\"193\",\"k\":\"2e81c89a22bbe369261cd349\",\"k2\":\"115db4936c1306a90\"},{\"no\":\"2\",\"size\":\"33260681\",\"seconds\":\"195\",\"k\":\"acecb7ae1229785928285b01\",\"k2\":\"13da998700c1a9b19\"},{\"no\":\"3\",\"size\":\"32049397\",\"seconds\":\"184\",\"k\":\"d1363e2d2773418a28285b01\",\"k2\":\"116c7411b641ebf14\"},{\"no\":\"4\",\"size\":\"26239628\",\"seconds\":\"172\",\"k\":\"b073b5f42c09ff3724114b92\",\"k2\":\"1a2d3f88fd57a9199\"},{\"no\":\"5\",\"size\":\"11971788\",\"seconds\":\"144\",\"k\":\"5dd4c1a78f5318b6261cd349\",\"k2\":\"114cc10b4027f3aab\"}],\"mp4\":[{\"no\":\"0\",\"size\":\"25680881\",\"seconds\":\"370\",\"k\":\"ca2dd9992e2b5c7828285b01\",\"k2\":\"18503c9e03807c420\"},{\"no\":\"1\",\"size\":\"25616910\",\"seconds\":\"324\",\"k\":\"3a5353841ae3019924114b92\",\"k2\":\"1c54d9f1059f382b8\"},{\"no\":\"2\",\"size\":\"22940093\",\"seconds\":\"383\",\"k\":\"e95e652bc302640524114b92\",\"k2\":\"17329db51b26ce492\"}],\"flv\":[{\"no\":\"0\",\"size\":\"13207340\",\"seconds\":\"369\",\"k\":\"af2eeaf8f0edd61024114b92\",\"k2\":\"1ac46dd2a5e08157a\"},{\"no\":\"1\",\"size\":\"14102060\",\"seconds\":\"344\",\"k\":\"05827f1c60cdd13e24114b92\",\"k2\":\"1e02e7eee20925185\"},{\"no\":\"2\",\"size\":\"11398419\",\"seconds\":\"363\",\"k\":\"85e1d43ba29ff1b928285b01\",\"k2\":\"1998eb8814bb80184\"}]},\"streamsizes\":{\"hd2\":\"162052111\",\"mp4\":\"74237884\",\"flv\":\"38707819\"},\"stream_ids\":{\"hd2\":\"112619097\",\"mp4\":\"112613057\",\"flv\":\"112606460\"},\"streamlogos\":{\"hd2\":0,\"mp4\":0,\"flv\":0},\"streamtypes\":[\"hd2\",\"mp4\",\"flv\"],\"streamtypes_o\":[\"hd2\",\"flvhd\",\"mp4\"]}],\"user\":{\"id\":0},\"controller\":{\"search_count\":true,\"mp4_restrict\":1,\"stream_mode\":1,\"video_capture\":true,\"area_code\":120000,\"dma_code\":4837,\"continuous\":0,\"playmode\":\"normal\",\"circle\":false,\"tsflag\":false,\"share_disabled\":false,\"download_disabled\":false,\"pc_disabled\":false,\"pad_disabled\":false,\"mobile_disabled\":false,\"tv_disabled\":false}}"
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
      val file_type = key match{
        case "flv" => "flv"
        case "hd2" => "flv"
        case "mp4" => "mp4"
        case _ => null
      }
      val url = "http://f.youku.com/player/getFlvPath/sid/00_%s/st/%s/fileid/%s%s%s?K=%s".format(
        no,file_type,vidlow,no,vidhigh,sm("k").asInstanceOf[String]
      )
      listBuf.append((new URL(url),sm("size").asInstanceOf[String].toInt))
    }
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
