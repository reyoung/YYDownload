package me.reyoung.yydownload.yyparser

import java.net.URL
import sun.misc.{BASE64Decoder, BASE64Encoder}
import collection.mutable.ArrayBuffer
import java.util.Date
import java.text.{SimpleDateFormat, DateFormat}
import xml.NodeSeq

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/25/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
object YoukuAuthorSubscriber extends IAuthorSubscriber with HttpUtil{
  def parse(url: URL): IAuthorSubscriberResult = {
    val uid = this.getUidByURL(url)
    new XMLSubscribeResult {
      this.setURL("http://www.youku.com/user/rss/id/"+uid)


      def VideoURLS(): Stream[String] = {
        val retv = new ArrayBuffer[String]()
        (this.RawVideoItems()\"link").foreach(
          p => {
            retv += p.text
          }
        )
        retv.toStream
      }

      def Limit(): Int = {
        (XML \\ "item").count(p => true)
      }

      def Name(): String = {
        (XML \\ "rss" \ "channel" \ "title")
          .text.replace("优酷-会员-","").replace("的最新视频","")
      }

      def VideoResults(defi:VideoDefinition.Type): Stream[IParseResult] =
        for (url <- this.VideoURLS()) yield {
          YoukuParser.parse(url,defi)
        }

      def BuildDate(): Date = {
        val buildDate = (XML \\"channel"\"lastBuildDate").text
        val fmt = new SimpleDateFormat("EEE, dd MMM yyyy H:m:s Z")
        fmt.parse(buildDate)
      }

      def RawVideoItems(): NodeSeq = {
        XML\\"item"
      }
    }
  }

  private def getUidByURL(url:URL) = {
    val status = this.retirePageStatus(url)
    if (status >=200 && status <400){
      //! Generate By URL String
      val urlStr = url.toString
      val rawUid = urlStr.substring(urlStr.lastIndexOf('/')+1)

      if (rawUid.head=='U'){
        val uidStr = new BASE64Decoder().decodeBuffer(rawUid.tail)
        var uid = 0
        uidStr.foreach((b)=>{
          uid *= 10
          uid += b - '0'
        })
        uid >>= 2
        uid
      } else {
        throw new SubscribePageError(url)
      }
    } else {
      throw new SubscribePageError(url)
    }
  }
}
