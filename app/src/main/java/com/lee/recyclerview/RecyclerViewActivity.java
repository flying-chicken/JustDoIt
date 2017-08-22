package com.lee.recyclerview;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;

import com.lee.justdoit.BaseActivity;
import com.lee.justdoit.R;
import com.lee.recyclerview.decoration.GridDecoration;
import com.lee.recyclerview.decoration.LinearDecoration;
import com.lee.tablayout.dummy.DummyContent;

public class RecyclerViewActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private LinearDecoration linearDecoration;
    private GridDecoration gridDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContent(R.layout.activity_recycler_view);

        initRecyclerView();
    }

    @Override
    protected void initParams() {
        super.initParams();
        bShowActionButton = false;
    }

    private void initRecyclerView(){
        linearDecoration = new LinearDecoration(this,LinearDecoration.VERTICAL);
        gridDecoration = new GridDecoration(this,GridDecoration.VERTICAL);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new RecyclerAdapter(this, DummyContent.ITEMS,RecyclerAdapter.STAGGERED);
        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DummyContent.DummyItem item) {
                Snackbar.make(rootLayout,item.content,Snackbar.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(gridDecoration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recycler_aty_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.recycler_menu_vertical:
                if(adapter.getType() != RecyclerAdapter.LINEAR){
                    setReyclerStyle(adapter.getType(), OrientationHelper.VERTICAL, gridDecoration);
                }else{
                    setReyclerStyle(RecyclerAdapter.LINEAR, OrientationHelper.VERTICAL, linearDecoration);
                }
                break;
            case R.id.recycler_menu_horizontal:
                if(adapter.getType() != RecyclerAdapter.LINEAR){
                    setReyclerStyle(adapter.getType(), OrientationHelper.HORIZONTAL, gridDecoration);
                }else{
                    setReyclerStyle(RecyclerAdapter.LINEAR, OrientationHelper.HORIZONTAL, linearDecoration);
                }
                break;
            case R.id.recycler_menu_linear:
                setReyclerStyle(RecyclerAdapter.LINEAR, OrientationHelper.VERTICAL, linearDecoration);
                break;
            case R.id.recycler_menu_grad:
                setReyclerStyle(RecyclerAdapter.GRID, OrientationHelper.VERTICAL, gridDecoration);
                break;
            case R.id.recycler_menu_staggered:
                setReyclerStyle(RecyclerAdapter.STAGGERED, OrientationHelper.VERTICAL, gridDecoration);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setReyclerStyle(int type, int orientation, RecyclerView.ItemDecoration d){
        adapter.setType(type);
        adapter.setOrientation(orientation);
        if(type == RecyclerAdapter.LINEAR){
            recyclerView.setLayoutManager(new LinearLayoutManager(this, orientation, false));
        }else if(type == RecyclerAdapter.GRID){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3, OrientationHelper.VERTICAL, false));
        }else{
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));
        }
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, orientation));
        recyclerView.removeItemDecoration(recyclerView.getItemDecorationAt(0));
        recyclerView.addItemDecoration(d);
        adapter.notifyDataSetChanged();
    }
}
