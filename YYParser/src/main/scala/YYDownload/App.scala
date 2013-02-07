package YYDownload
//import me.reyoung.yydownload.yyparser._
import me.reyoung.yydownload.yyparser.{VideoDefinition, ParserFactory, YoukuListParser}
import java.net.URL

/**
 * Hello world!
 *
 */
object MainApp extends  App{
  val result =  ParserFactory.parse(new URL("http://v.youku.com/v_show/id_XNDY3MDM2Mzgw.html?f=18476474"),
    VideoDefinition.NORMAL,
    (_,_)=>{})
  println(result)
}
