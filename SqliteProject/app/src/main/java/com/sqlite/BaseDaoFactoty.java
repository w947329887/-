package com.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * Created by m.wang on 2018/9/10.
 */
public class BaseDaoFactoty {
    private static final  BaseDaoFactoty ourInstance = new BaseDaoFactoty();
    public static BaseDaoFactoty getOurInstance(){
        return ourInstance;
    }

    private SQLiteDatabase sqLiteDatabase;

    private String sqliteDatabasePath;

    private BaseDaoFactoty(){
        sqliteDatabasePath= Environment.getExternalStorageDirectory()+"/a/wm.db";
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(sqliteDatabasePath,null);
    }

    public <T>BaseDao<T> getBaseDao(Class<T> entityClass){
        BaseDao baseDao = null;
        try {
            baseDao = BaseDao.class.newInstance();
            baseDao.init(sqLiteDatabase,entityClass);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return baseDao;
    }
}
