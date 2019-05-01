package com.xz.bing;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * 本地缓存界面
 * 继承与第三方activity 实现左滑返回 依赖：'me.imid.swipebacklayout.lib:library:1.1.0'
 * github:https://github.com/ikew0ng/SwipeBackLayout
 */
public class LocalPicture extends SwipeBackActivity {

    private static final String TAG = "LocalPicture";
    private List<LocalPic> localPicList = new ArrayList<>();
    private LocalPicAdapter adapter;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;

    private EditText searchText;

    /**
     * 重新回到此活动是刷新适配器
     */
    @Override
    protected void onResume() {
        super.onResume();
        //初始化图片数据并更新图片适配器
        initLocalPicData(new initLocalPicDataCallbackListener() {
            @Override
            public void finish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_picture);
        getWindow().setEnterTransition(new Slide().setDuration(300));
        getWindow().setExitTransition(new Slide().setDuration(300));

        //设置toolbar为默认标题栏
        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local_pic);
        setSupportActionBar(toolbar);
        //开启返回箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //活动标题
        setTitle("本地缓存");
        //初始化对象Recycler列表
        recyclerView = (RecyclerView) findViewById(R.id.local_image_recycler_view);
        layoutManager = new GridLayoutManager(LocalPicture.this, 1);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new LocalPicAdapter(localPicList);
        recyclerView.setAdapter(adapter);

        searchText = (EditText) findViewById(R.id.search_local_pic);
        searchText.setHintTextColor(Color.WHITE);


        initListeners();
    }

    /**
     * 监听事件
     */
    private void initListeners() {
        //搜索框内容监听
        searchText.addTextChangedListener(new TextWatcher() {
            //索引
            int i = 0;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 文本实时变化的回调
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                // 当文本被改变前的回调
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // 文本变化以后的回调
                for (LocalPic localPic : localPicList) {
                    if (localPic.getEnddate().equals(editable.toString())) {
                        //滚动到指定条目，有滚动效果
//                        recyclerView.scrollToPosition(i);//没有滚动效果
                        recyclerView.smoothScrollToPosition(i);
                    }
                    i++;
                }
                i = 0;
            }
        });
    }


    /**
     * 初始化本地数据
     * 这里使用回调机制
     * 防止用户本地照片过多导致初始化过久
     */
    private void initLocalPicData(initLocalPicDataCallbackListener callbackListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //先清空下集合
                localPicList.clear();
                //本地缓存路径
                String dir = getExternalFilesDir("image").getPath();
                //sharedPreferences读取
                SharedPreferences pref;
                //文件路径
                File file = new File(dir);
                //获取文件列表
                File[] files = file.listFiles();
                //创建集合并添加为元素
                List<File> fileList = new ArrayList<File>();
                for (File f : files) {
                    fileList.add(f);
                }
                //以文件名排序，这样就选修改了文件的日期本地缓存图片排序也没有影响
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o2.getName().compareTo(o1.getName());
                    }
                });
                //        Collections.reverse(fileList);//反转集合
                for (File f : fileList) {
                    //s.split(".jpg")[0];//切割字符串获取enddate
                    pref = getSharedPreferences(f.getName().split(".jpg")[0], 0);
                    localPicList.add(
                            new LocalPic(pref.getString("enddate", "null"),
                                    pref.getString("uri", "null"),
                                    pref.getString("copyright", "null")));
                    if (pref != null)
                        pref = null;
                }

                callbackListener.finish();
            }
        }).start();


    }


    /**
     * 在标题栏加载按钮
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_local_pic, menu);
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
            case R.id.search_item_local_pic:
                //打开搜索框
                Search();
                break;
            case android.R.id.home:
                //返回箭头按钮监听
                finish();
                break;
            default:
                break;

        }
        return true;
    }

    private void Search() {

        //再次点击关闭搜索框
        if (searchText.getVisibility() == View.VISIBLE) {
            //隐藏搜索框
            searchText.setVisibility(View.GONE);


        } else if (searchText.getVisibility() == View.GONE) {
            //显示搜索框
            searchText.setVisibility(View.VISIBLE);


        }


    }
}
