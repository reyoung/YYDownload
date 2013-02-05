package YYDownload
import me.reyoung.yydownload.yyparser._

/**
 * Hello world!
 *
 */
object MainApp extends  App{
  val youku = new YoukuParser()
  val result = youku.parse("http://www.youku.com/v_show/id_XNTA3MjYwMDg4_rss.html",
      VideoDefinition.NORMAL)
  if(result!=null){
    println("parse success")
    println(result.getTitle)
    println(result.DownloadUrls())
    println(result.isInstanceOf[AuthorIDTrait])
  }
}
