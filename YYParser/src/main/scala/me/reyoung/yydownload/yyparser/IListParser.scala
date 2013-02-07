package me.reyoung.yydownload.yyparser

import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/7/13
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */


trait IListParseResult extends AuthorNameTrait{
  def Title():String
}

trait IListParser extends HttpUtil {
  val SiteDescription:String

  def parse(url:URL):IListParseResult

  final def parse(url:String):IListParseResult = parse(new URL(url))

}
