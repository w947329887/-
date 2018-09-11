package com.sqlite;


/**
 * Created by m.wang on 2018/9/10.
 * 操作的一個顶层接口
 */
public interface IBaseDao<T>{

    /** 插入操作 */
    long insert(T entity);

    /** 删除操作 */
    int delete(String condition,String[] value);

}
