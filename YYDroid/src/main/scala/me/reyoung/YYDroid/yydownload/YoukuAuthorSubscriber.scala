package me.reyoung.YYDroid.yydownload
import me.reyoung.yydownload.yyparser.YoukuAuthorSubscriber
/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/25/13
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
class AndYoukuAuthorSubscriber extends YoukuAuthorSubscriber{
  override protected def base64(str:String) = {
    android.util.Base64.decode(str,0)
  }
}