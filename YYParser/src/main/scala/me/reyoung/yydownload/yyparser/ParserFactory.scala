package me.reyoung.yydownload.yyparser

import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/7/13
 * Time: 6:46 PM
 * To change this template use File | Settings | File Templates.
 */
object ParserFactory {
  val Parsers = Array(YoukuListParser,YoukuParser)

  private def defaultProcess(a:Int,b:Int){}

  def parse(url:URL,definition:VideoDefinition.Type=VideoDefinition.NORMAL, process:(Int,Int)=>Unit =defaultProcess):Option[Any]={
    var retv:Any = null

    for (p <- Parsers if retv==null ){
      retv = try {
        p match {
          case video_parser:IParser => {
            video_parser.parse(url,definition)
          }
          case list_parser:IListParser => {
            list_parser.parse(url,definition,process)
          }
          case _ => null
        }
      } catch {
        case _ => null
      }
    }
    if (retv == null) {
      None
    } else {
      new Some[Any](retv)
    }
  }

//  final def parse(url:String,definition:VideoDefinition.Type=VideoDefinition.NORMAL,process:(Int,Int)=>Unit =defaultProcess):Option[Any]={
//    this.parse(new URL(url),definition,process)
//  }

  def parseVideo(url:URL,definition:VideoDefinition.Type = VideoDefinition.NORMAL) :Option[IParseResult] = {
    var retv :IParseResult = null
    for (p <- Parsers if retv==null){
      retv = p match {
        case _:IListParser => null
        case parser:IParser => try {
          parser.parse(url,definition)
        } catch {
          case _ => null
        }
        case _ => null
      }
    }
    if (retv == null)
      None
    else
      new Some[IParseResult](retv)
  }

  final def parseVideo(url:String,definition:VideoDefinition.Type):Option[IParseResult]
    = this.parseVideo(new URL(url), definition)


  def parseList(url:URL,definition:VideoDefinition.Type = VideoDefinition.NORMAL,process:(Int,Int)=>Unit=defaultProcess)
    :Option[IListParseResult] = {
    var retv:IListParseResult = null
    for (p<-Parsers if retv == null){
      retv = p match {
        case _:IParser => null
        case parser:IListParser => try{
          parser.parse(url,definition,process)
        } catch {
          case _ => null
        }
        case _ => null
      }
    }
    if (retv == null)
      None
    else
      new Some[IListParseResult](retv)
  }

//  def parseList(url:String,definition:VideoDefinition.Type=VideoDefinition.NORMAL,process:(Int,Int)=>Unit=defaultProcess):Option[IListParseResult]
//    = parseList(new URL(url),definition,process)
}
