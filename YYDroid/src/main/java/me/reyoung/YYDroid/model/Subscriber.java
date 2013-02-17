package me.reyoung.YYDroid.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created with IntelliJ IDEA.
 * User: reyoung
 * Date: 2/17/13
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
@DatabaseTable(tableName = "subscribers")
public class Subscriber {
    @DatabaseField(generatedId = true,allowGeneratedIdInsert = false)
    private int Id;
    @DatabaseField(canBeNull = false,width=255)
    private String Url;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
