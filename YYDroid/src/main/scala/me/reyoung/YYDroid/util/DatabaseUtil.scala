package me.reyoung.YYDroid.util

import android.content.Context
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import me.reyoung.YYDroid.model.Subscribe
import android.util.Log
import java.sql.SQLException

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 5:51 PM
 * To change this template use File | Settings | File Templates.
 */
object DatabaseUtil{
  val DATABASE_NAME= "YYDroid.db"
  val DATABASE_VERSION=0
  val TABLES = Array(classOf[Subscribe])
}

class DatabaseUtil(val context:Context) extends OrmLiteSqliteOpenHelper(
  context,DatabaseUtil.DATABASE_NAME,null,DatabaseUtil.DATABASE_VERSION
) with LogTag {
  def onCreate(db: SQLiteDatabase, cs: ConnectionSource) {
    try{
      Log.d(LogTag,"Creating Tables")
      DatabaseUtil.TABLES.foreach(cls=>TableUtils.createTable(cs,cls))
    } catch {
      case ex:SQLException =>{
        Log.e(LogTag, "Unable to create databases",ex)
      }
    }
  }

  def onUpgrade(db: SQLiteDatabase, cs: ConnectionSource, oldVer: Int, newVer: Int) {
    try {
      DatabaseUtil.TABLES.reverse.foreach(cls=>TableUtils.dropTable(cs,cls,true))
      this.onCreate(db,cs)
    } catch {
      case ex:SQLException => {
        Log.e(LogTag,"Unable to update databases",ex)
      }
    }
  }
}
