package me.reyoung.YYDroid.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.reyoung.YYDroid.model.Subscriber;

import java.sql.SQLException;

public class DatabaseUtil extends OrmLiteSqliteOpenHelper {
    static final private String DATABASE_NAME= "YYDroid.db";
    static final private int DATABASE_VERSION=2;
    static final private Class TABLES[] = {
            Subscriber.class
    };
    private Dao<Subscriber, Integer> subscriberDao=null;

    public DatabaseUtil(Context ctx){
        super(ctx,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            Log.d(LogTag(),"Create Tables");
            for (Class cls :DatabaseUtil.TABLES){
                TableUtils.createTable(connectionSource,cls);
            }
        } catch (SQLException ex){
            Log.e(LogTag(),"Unable to create database",ex);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            Log.d(LogTag(),"Update Table");
            for (int k=0;i<TABLES.length;++k){
                TableUtils.dropTable(connectionSource,TABLES[TABLES.length-k],true);
            }
        } catch (SQLException e) {
            Log.e(LogTag(),"Unable to update database",e);
        }
    }

    public String LogTag() {
        return "YYDroid_"+this.getClass().getSimpleName();
    }

    public Dao<Subscriber, Integer> getSubscriberDao() throws SQLException {
        if(subscriberDao==null){
            subscriberDao = getDao(Subscriber.class);
        }
        return subscriberDao;
    }
}