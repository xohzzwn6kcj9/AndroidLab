package com.example.student.stock;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

import java.util.ArrayList;
import java.util.List;





public class DetailMain extends AppCompatActivity implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener, OnNavigationListener {

    //add1 ------------------------------------
    TabHost host;
    ViewPager viewPager;
    DetailPagerAdapter pagerAdapter;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    class TabFactory implements TabContentFactory {

        @Override
        public View createTabContent(String tag) {
            final View dummy = new View(getApplicationContext());
            dummy.setMinimumHeight(0);
            dummy.setMinimumWidth(0);
            return dummy;
        }
    }


	//add1 end ----------------------------------


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null){
            setSupportActionBar(toolbar);
        }

        //List Navigation
        String[] favorites={"삼성전자","현대차","아시아나항공"};
        ArrayAdapter<String> list = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item,favorites);
        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setTitle("");

        // Initialise the TabHost
        initTab();
        // Intialise ViewPager
        initViewPager();

        //add2------------------------------
        drawer = (DrawerLayout) findViewById(R.id.detail_main_drawer);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

		//add2 end ------------------------------

        drawer.setDrawerListener(toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initTab() {
        host = (TabHost) findViewById(R.id.tabhost);
        host.setup();

        TabSpec tabSpec=null;

        tabSpec=host.newTabSpec("current");
        tabSpec.setIndicator("시세");
        tabSpec.setContent(new TabFactory());
        host.addTab(tabSpec);

        tabSpec=host.newTabSpec("order");
        tabSpec.setIndicator("주문");
        tabSpec.setContent(new TabFactory());
        host.addTab(tabSpec);

        tabSpec=host.newTabSpec("news");
        tabSpec.setIndicator("뉴스");
        tabSpec.setContent(new TabFactory());
        host.addTab(tabSpec);

        tabSpec=host.newTabSpec("info");
        tabSpec.setIndicator("정보");
        tabSpec.setContent(new TabFactory());
        host.addTab(tabSpec);

        tabSpec=host.newTabSpec("movie");
        tabSpec.setIndicator("진단");
        tabSpec.setContent(new TabFactory());
        host.addTab(tabSpec);

        host.setOnTabChangedListener(this);
    }


    private void initViewPager() {

        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(Fragment.instantiate(this, DetailTabCurrentFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, DetailTabOrderFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, DetailTabNewsFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, DetailTabInfoFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, DetailTabMovieFragment.class.getName()));

        pagerAdapter = new DetailPagerAdapter(
                super.getSupportFragmentManager(), fragments);
        //
        viewPager = (ViewPager) super.findViewById(R.id.viewpager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }





    //Tab 이 변경 되었을때 Pager 도 변경되는 효과를 주기 위해서
    @Override
    public void onTabChanged(String tag) {
        // TabInfo newTab = this.mapTabInfo.get(tag);
        int pos = host.getCurrentTab();
        viewPager.setCurrentItem(pos);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        // TODO Auto-generated method stub

    }

    //Pager 에 의해 화면이 변경되었을때 Tab 의 버튼도 변경되게 하기 위해서..
    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        host.setCurrentTab(position);
    }


    @Override
    public void onPageScrollStateChanged(int state) {
        // TODO Auto-generated method stub

    }
    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // TODO Auto-generated method stub
        return false;
    }

    //Navigation Drawer
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
}
