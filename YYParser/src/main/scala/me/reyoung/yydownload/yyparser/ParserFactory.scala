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
  val Parsers = Array(YoukuListParser,YoukuParser,YoukuAuthorSubscriber)

  def parse(url:URL,definition:VideoDefinition.Type):Option[Any]={
    var retv:Any = null

    for (p <- Parsers if retv==null ){
      retv = try {
        p match {
          case video_parser:IParser => {
            video_parser.parse(url,definition)
          }
          case list_parser:IListParser => {
            list_parser.parse(url,definition)
          }
          case subscribe:IAuthorSubscriber =>{
            subscribe.parse(url)
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

  final def parse(url:String,definition:VideoDefinition.Type):Option[Any]={
    this.parse(new URL(url),definition)
  }

  def parseVideo(url:URL,definition:VideoDefinition.Type) :Option[IParseResult] = {
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


  def parseSubscribe(url:URL):Option[IAuthorSubscriberResult] = {
    var retv:IAuthorSubscriberResult = null
    for (p <- Parsers if retv == null) {
      retv = p match {
        case parser:IAuthorSubscriber => try {
          parser.parse(url)
        } catch {
          case _ => {null}
        }
        case _ => {null}
      }
    }
    if (retv == null)
      None
    else
      Some(retv)
  }

  final def parseSubscribe(url:String):Option[IAuthorSubscriberResult] = {
    this.parseSubscribe(new URL(url))
  }

  def parseList(url:URL,definition:VideoDefinition.Type)
    :Option[IListParseResult] = {
    var retv:IListParseResult = null
    for (p<-Parsers if retv == null){
      retv = p match {
        case _:IParser => null
        case parser:IListParser => try{
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
      new Some[IListParseResult](retv)
  }

  def parseList(url:String,definition:VideoDefinition.Type):Option[IListParseResult]
    = parseList(new URL(url),definition)
}
