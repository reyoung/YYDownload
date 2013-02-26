package me.reyoung.YYDroid.util

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity
import collection.JavaConversions._
import android.util.Log
import me.reyoung.YYDroid.model.Subscriber
import android.widget.Toast
import me.reyoung.R
import me.reyoung.YYDroid.yydownload.AndroidParserFactory

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/26/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
trait SubscribeTrait extends OrmLiteBaseActivity[DatabaseUtil] with LogTag{

  protected def subscribeURL(url:String){
    if (AndroidParserFactory.parseSubscribe(url)!=null){
      // Append Subscribe
      val dao = this.getHelper.getSubscriberDao
      val maps = Map[String,AnyRef](
        "Url"->url
      )
      val result = dao.queryForFieldValuesArgs(maps)
      if (result.size()==0){
        val sub = new Subscriber
        sub.setUrl(url)
        val newID = dao.create(sub)
        Log.d(LogTag,"Create Subscriber "+newID)
      } else {
        Toast.makeText(this,R.string.subscribe_already_fails,Toast.LENGTH_LONG)
      }
    } else {
      Toast.makeText(this,R.string.subscribe_fails,Toast.LENGTH_LONG).show()
    }

//    Log.d(LogTag,"Found Sql Result Count "+result.size())
  }
}
