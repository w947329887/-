package com.bean;

import com.sqlite.DBField;
import com.sqlite.DBTable;

/**
 * Created by m.wang on 2018/9/10.
 */
@DBTable("tb_user")
public class User {
    @DBField("id")
    private Integer id;
    @DBField("name")
    private String name;
    @DBField("password")
    private String password;

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }
}
