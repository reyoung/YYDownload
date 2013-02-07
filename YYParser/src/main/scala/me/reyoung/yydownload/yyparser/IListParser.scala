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
  def Videos():List[IParseResult]
}

trait IListParser extends HttpUtil {
  val SiteDescription:String

  def parse(url:URL,vd:VideoDefinition.Type,process:(Int,Int)=>Unit):IListParseResult

  final def parse(url:String,vd:VideoDefinition.Type,process:(Int,Int)=>Unit):IListParseResult = parse(new URL(url),vd,process)
  final def parse(url:String):IListParseResult =
    parse(new URL(url), VideoDefinition.NORMAL,(id:Int,len:Int)=>{
      print("processing %d:%d\n".format(id,len))
    })

}
