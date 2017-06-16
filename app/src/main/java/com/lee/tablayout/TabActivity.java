package com.lee.tablayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.lee.justdoit.BaseActivity;
import com.lee.justdoit.R;
import com.lee.tablayout.dummy.DummyContent;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends BaseActivity implements TabItemFragment.OnListFragmentInteractionListener {

    private ViewPager viewPager;
    private MAdpapter mAdpapter;
    private List<String> titles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tab);

//        setAtyTitle("TAB Activity");
        setContent(R.layout.content_tab);

        initViewPager();
        initTabs();
    }

    @Override
    protected void initParams() {
        super.initParams();
        bShowTabLayout = true;
        bShowActionButton = false;
    }

    private void initViewPager(){
        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        mAdpapter = new MAdpapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdpapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tablayout_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //当tabmode 为MODE_SCROLLABLE时,会忽略tabgravity属性
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
//        tabLayout = (TabLayout) findViewById(R.id.tablayout_tab);
        addTabs(3);
    }

    private void addTabs(int count){
        tabLayout.removeAllTabs();
        titles.clear();
        fragments.clear();
        for(int i=1; i<= count; i++){
            tabLayout.addTab(tabLayout.newTab().setText("TAB"+i));
            titles.add("Page "+i);
//            fragments.add(TabFragment.newInstance(i));
            fragments.add(TabItemFragment.newInstance(10));
        }
        viewPager.getAdapter().notifyDataSetChanged();
    }

    //使用support.v4.Fragment时，需要该Activity继承 Fragment.OnFragmentInteractionListener接口，与Fragment进行交互
//    @Override
//    public void onFragmentInteraction(Uri uri) {
//        //当 Fragment中 ，有事件调用了该接口，Activity会在这里接收回调
//        //Toast.makeText(this, ""+uri, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Snackbar.make(rootLayout,item.details,Snackbar.LENGTH_SHORT).show();
    }

    private class MAdpapter extends FragmentPagerAdapter{

        public MAdpapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
