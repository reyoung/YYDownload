package me.reyoung.yydownload.yyparser

import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/7/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */


trait IListParseResult{
  def Title():String
  def Videos():Stream[IParseResult]
}

trait IListParser extends HttpUtil {
  val SiteDescription:String

  def parse(url:URL,vd:VideoDefinition.Type):IListParseResult

  final def parse(url:String,vd:VideoDefinition.Type):IListParseResult = parse(new URL(url),vd)
  final def parse(url:String):IListParseResult =
    parse(new URL(url), VideoDefinition.NORMAL)

}
