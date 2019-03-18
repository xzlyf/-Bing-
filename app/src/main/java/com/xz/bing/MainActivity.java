package com.xz.bing;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    //滑动菜单对象
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置toolbar为默认标题栏
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化滑动菜单
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //让标题栏显示滑动菜单的按钮显出来
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //NavigationView滑动菜单初始化及监听事件
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.setting_nav_item:
                            Toast.makeText(MainActivity.this,"设置",Toast.LENGTH_SHORT).show();
                    }
                    return true;
            }
        });

    }

    /**
     * 在标题栏加载按钮
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 标题栏按钮事件监听
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_item:
                Toast.makeText(this, "搜索", Toast.LENGTH_SHORT).show();
                break;
            case R.id.exit_itme:
                Toast.makeText(this, "exit", Toast.LENGTH_SHORT).show();
                break;

            case R.id.about_item:
                Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
                break;

                //监听主页键
            case android.R.id.home:
                //显示出滑动菜单
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;

        }
        return true;
    }
}
