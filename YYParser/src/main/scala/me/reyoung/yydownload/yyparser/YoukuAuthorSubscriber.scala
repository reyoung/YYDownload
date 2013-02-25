package me.reyoung.yydownload.yyparser

import java.net.URL
import sun.misc.{BASE64Decoder, BASE64Encoder}

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/25/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
class YoukuAuthorSubscriber extends IAuthorSubscriber with HttpUtil{
  def parse(url: URL): IAuthorSubscriberResult = {
    val uid = this.getUidByURL(url)
    print("UID is "+uid)
    null
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
