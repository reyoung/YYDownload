package me.reyoung.YYDroid.model

import com.j256.ormlite.table.DatabaseTable
import com.j256.ormlite.field.DatabaseField
import reflect.BeanProperty

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/16/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
@DatabaseTable(tableName = "subscribes")
class Subscribe extends Serializable{
  @BeanProperty
  @DatabaseField(generatedId = true)
  var Id:Int = 0

  @DatabaseField
  @BeanProperty
  var Url:String = null
}
