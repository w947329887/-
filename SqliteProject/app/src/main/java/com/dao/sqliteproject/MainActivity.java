package com.dao.sqliteproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bean.User;
import com.sqlite.BaseDao;
import com.sqlite.BaseDaoFactoty;


public class MainActivity extends AppCompatActivity {

    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDao<User> userDao = BaseDaoFactoty.getOurInstance().getBaseDao(User.class);
                userDao.insert(new User(1,".","w947329887"));
                Toast.makeText(MainActivity.this,"执行成功",Toast.LENGTH_LONG).show();
            }
        });
    }
}
