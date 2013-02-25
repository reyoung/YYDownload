package me.reyoung.yydownload.yyparser

import java.net.URL
import xml.NodeSeq

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/25/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
trait IAuthorSubscriber {
  def parse(url:URL):IAuthorSubscriberResult

  def parse(url:String):IAuthorSubscriberResult =
    this.parse(new URL(url))
}

trait IAuthorSubscriberResult {
  def Name():String
  def VideoURLS():Stream[String]
  def VideoResults(defi:VideoDefinition.Type):Stream[IParseResult]
  def Limit():Int

}

trait IAuthorSubscribeException extends Exception

case class SubscribePageError(url:URL)
  extends RuntimeException("Subscribe URL "+url +" Retire Error Or it's not an author page")
  with IAuthorSubscribeException{
}