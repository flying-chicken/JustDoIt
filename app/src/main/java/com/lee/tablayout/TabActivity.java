package com.lee.tablayout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;

import com.lee.justdoit.R;

public class TabActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tablayout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //当tabmode 为MODE_SCROLLABLE时，会忽略tabgravity属性
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.tabmode_fixed:
                tabLayout.setTabMode(TabLayout.MODE_FIXED);
                addTabs(3);
                break;
            case R.id.tabmode_scrollable:
                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                addTabs(8);
                break;
            case R.id.tabgravity_center:
                tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
                break;
            case R.id.tabgravity_fill:
                tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTabs(){
        tabLayout = (TabLayout) findViewById(R.id.tablayout_tab);
        addTabs(3);
    }

    private void addTabs(int count){
        tabLayout.removeAllTabs();
        for(int i=1; i<= count; i++){
            tabLayout.addTab(tabLayout.newTab().setText("TAB"+i));
        }
    }
}
