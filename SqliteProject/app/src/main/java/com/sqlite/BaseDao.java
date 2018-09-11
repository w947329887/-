package com.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by m.wang on 2018/9/10.
 */
public class BaseDao<T> implements IBaseDao<T>{
    //操作数据库，持有数据库操作的引用
    private SQLiteDatabase sqLiteDatabase;
    //持有操作数据库所对应的java类型
    private Class<T> entityClass;
    //表名
    private String tableName;
    //标记，用来是否已经存在
    private boolean isInit = false;

    //定义一个缓存空间(key - 字段名 )
    private HashMap<String,Field> cacheMap;

    public boolean init(SQLiteDatabase sqLiteDatabase, Class<T> entityClass) {
        this.sqLiteDatabase = sqLiteDatabase;
        this.entityClass = entityClass;
        //自动建表(只需要建一次)
        if (!isInit){
            //如果没有建过表就建一张新表
            tableName=entityClass.getAnnotation(DBTable.class).value();
            if (!sqLiteDatabase.isOpen()){
                return false;
            }
            //执行自动建表的动作
            String creatTableSql = getCreateTableSql();
            sqLiteDatabase.execSQL(creatTableSql);
            isInit = true;

            //初始化缓存空间
            cacheMap = new HashMap<>();
            initCacheMap();
        }
        return isInit;
    }

    private void initCacheMap() {
        //1.取到所有得列表
        String sql = "select * from " + tableName +" limit 1,0";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        String[] columnNames = cursor.getColumnNames();
        //2.取所有得成员变量
        Field[] clounmnFields = entityClass.getDeclaredFields();
        //3.通过2层循环让他们对应起来
        for (String columnName: columnNames){
            Field resultField = null;
            for (Field field:clounmnFields){
                String fieldAnnotionName = field.getAnnotation(DBField.class).value();
                if (columnName.equals(fieldAnnotionName)){
                    resultField = field;
                    break;
                }
            }
            if (resultField != null){
                cacheMap.put(columnName,resultField);
            }
        }
    }

    /***
     * 自动创建表
     * @return
     */
    private String getCreateTableSql() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("create table if not exists ");
        stringBuffer.append(tableName+"(");
        //反射得到所有的成员变量
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field: fields){
            Class type = field.getType();
            if (type == String.class){
                stringBuffer.append(field.getAnnotation(DBField.class).value()+" TEXT,");
            }else if (type == Integer.class){
                stringBuffer.append(field.getAnnotation(DBField.class).value()+" INTEGER,");
            }else if (type == Long.class){
                stringBuffer.append(field.getAnnotation(DBField.class).value()+" BIGINT,");
            }else if (type == Double.class){
                stringBuffer.append(field.getAnnotation(DBField.class).value()+" DOUBLE,");
            }else if (type == byte[].class){
                stringBuffer.append(field.getAnnotation(DBField.class).value()+" BLOB,");
            }else {
                continue;
            }
        }
        if (stringBuffer.charAt(stringBuffer.length() - 1) == ','){
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    /**
     *
     * @param entity
     * @return
     */
    @Override
    public long insert(T entity) {
        //1.准备好ContentValues
        Map<String,String> map = getValues(entity);
        //2.插入内容
        ContentValues values = getContentValues(map);
        //3.执行插入
        long result = sqLiteDatabase.insert(tableName,null,values);
        return result;
    }

    /**
     * 删除操作
     * @param condition
     * @return
     */
    @Override
    public int delete(String condition,String[] value) {
        sqLiteDatabase.delete(tableName,condition,value);
        return 0;
    }


    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);
            if (value != null){
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    private Map<String,String> getValues(T entity) {
        HashMap<String,String> map = new HashMap<>();
        Iterator<Field> fieldItertor = cacheMap.values().iterator();
        while (fieldItertor.hasNext()){
            Field field = fieldItertor.next();
            field.setAccessible(true);
            //获取变量的值
            try {
                Object object = field.get(entity);
                if (object == null){
                    continue;
                }
                String value = object.toString();
                //获取别名 _id name password
                String key = field.getAnnotation(DBField.class).value();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)){
                    map.put(key,value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
