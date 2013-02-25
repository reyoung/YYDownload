package me.reyoung.yydownload.yyparser

import xml.NodeSeq
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/25/13
 * Time: 4:46 PM
 * To change this template use File | Settings | File Templates.
 */
trait XMLSubscribeResult extends IAuthorSubscriberResult with HttpUtil{
  var XML:NodeSeq = null
  def BuildDate():Date
  def RawVideoItems():NodeSeq
  protected def setURL(url:String){
    XML = this.retirePageXML(url)
//    println(XML)
  }
}
