package me.reyoung.YYDroid.yydownload

import me.reyoung.yydownload.yyparser.{IAuthorSubscriber, IAuthorSubscriberResult}
import java.net.URL
import me.reyoung.YYDroid.util.LogTag
import android.util.Log

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/26/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
object AndroidParserFactory  extends LogTag {
  val Parsers = Array(new AndYoukuAuthorSubscriber)

  def parseSubscribe(url:URL):IAuthorSubscriberResult = {
    var retv:IAuthorSubscriberResult = null
    for(p <- this.Parsers if retv == null) {
      retv = try{
        p match {
          case asp:IAuthorSubscriber => {
            asp.parse(url)
          }
          case _ => {
            null
          }
        }
      } catch {
        case ex:Exception => {
          Log.i(LogTag,"During Parsing "+url+" with parser "+p.getClass.getSimpleName+" error",ex)
          null
        }
      }
    }
    retv
  }
  def parseSubscribe(url:String):IAuthorSubscriberResult
    =try {
    this.parseSubscribe(new URL(url))
  } catch {
    case _:Exception => {null}
  }
}
