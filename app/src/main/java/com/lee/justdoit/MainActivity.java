package com.lee.justdoit;

import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lee.adapter.MainRecyclerAdapter;
import com.lee.tablayout.TabActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private List<String> items;
    private List<String> activities;

    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbar();
        setNavigationView();
        setCollapsingToolbarLayout();

        init();
        snackbarDemo();
        initRecyclerView();
    }

    private void init(){
        items = new ArrayList<String>();
        items.add("TabLayout");
        items.add("Glide");
        items.add("RecyclerView");
        items.add("TestActivity");
        items.add("SelectImageActivity");
        activities = new ArrayList<>();
        activities.add("com.lee.tablayout.TabActivity");
        activities.add("com.lee.glide.GlideActivity");
        activities.add("com.lee.recyclerview.RecyclerViewActivity");
        activities.add("com.lee.justdoit.TestActivity");
        activities.add("com.lee.photo.SelectImageActivity");
    }

    private void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setCollapsingToolbarLayout(){
        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.main_appbarlayout);
        appBarLayout.setExpanded(false);
        //用该layout包裹Toolbar时，可以设置Toolba的标题
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_ctlayout);
        collapsingToolbarLayout.setTitle("JUST GO");
    }

    private void setNavigationView(){
        /* 显示Toolbar 左上角菜单图标需要 ：
         * 1、styls里面 新建主题AppTheme.NoActionBar 设置<item name="windowActionBar">false</item>  <item name="windowNoTitle">true</item>
         * 2、manifest文件中，给application标签设置 theme android:theme="@style/AppTheme.NoActionBar"
         * 3、按照xml布局文件中的 顺序 放置 DrawerLayout 和 NavigationView
         * 4、在代码中 设置好Toolbar，看setToolbar方法
         * 5、设置好drawerlayout 及DrawerToggle，注意，drawertoggle必须用下面的构造方法，且 在postOnCreate方法中 设置 drawertoggle.syncState()设置
         *    初始状态
         * 6、设置 getSupportActionBar().setHomeButtonEnabled(true); getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         */
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerlayout);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.main_navigationview);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Toast.makeText(getApplicationContext(),item.getItemId()+"",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void snackbarDemo(){
        final CoordinatorLayout root = (CoordinatorLayout) findViewById(R.id.activity_root);
        findViewById(R.id.main_floating_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // LENGTH_INDEFINITE 只有点击action时才会消失
                Snackbar.make(root,"HELLO SNACKBAR",Snackbar.LENGTH_SHORT).setAction("BYE", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
            }
        });
    }

    public void toTabActivity(View view){
        startActivity(new Intent(this, TabActivity.class));
    }

    private void toActivity(String aty){
        try {
           Intent i = new Intent(this,Class.forName(aty));
//            i.setComponent(new ComponentName())
            startActivity(i);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.activity_root),"未找到该类",Snackbar.LENGTH_SHORT).show();
        }

    }

    public void loadImage(View view){
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1490074741640&di=f98224ec4a876da82504d5c4ee79803d&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F17%2F16%2F41%2F06G58PICfVz_1024.jpg";
//        ImageView imageView = (ImageView) findViewById(R.id.main_image);
//        Glide.with(this).load(url).into(imageView);
    }

    private void initRecyclerView(){
        MainRecyclerAdapter adapter = new MainRecyclerAdapter(this,items);
        adapter.setOnItemClickListener(new MainRecyclerAdapter.MainItemClickListener() {
            @Override
            public void onItemClickListener(View view, int position) {
                toActivity(activities.get(position));
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //是否去掉padding，默认为true（为true时，设置的padding无效）
        //
        recyclerView.setClipToPadding(false);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
