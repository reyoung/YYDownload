package YYDownload
//import me.reyoung.yydownload.yyparser._
import me.reyoung.yydownload.yyparser.YoukuListParser

/**
 * Hello world!
 *
 */
object MainApp extends  App{
  val youku = new YoukuListParser()
  youku.parse("http://v.youku.com/v_show/id_XNDY3NzIyNDA4.html?f=18476474")
}
