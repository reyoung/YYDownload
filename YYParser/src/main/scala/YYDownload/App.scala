package YYDownload
//import me.reyoung.yydownload.yyparser._
import me.reyoung.yydownload.yyparser.{YoukuAuthorSubscriber, VideoDefinition, ParserFactory, YoukuListParser}
import java.net.URL

/**
 * Hello world!
 *
 */
object MainApp extends  App{
  val result = ParserFactory.parse("http://i.youku.com/u/UMTE0NDEzOTky"
      ,VideoDefinition.NORMAL)
  println(result)

}
