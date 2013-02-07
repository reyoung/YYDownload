package YYDownload
//import me.reyoung.yydownload.yyparser._
import me.reyoung.yydownload.yyparser.YoukuListParser

/**
 * Hello world!
 *
 */
object MainApp extends  App{
  val youku = YoukuListParser
  val result =  youku.parse("http://v.youku.com/v_show/id_XNDY3NzIyNDA4.html")
  println(result)
//  println("Video Count In these list "+result.Videos().length)
}
