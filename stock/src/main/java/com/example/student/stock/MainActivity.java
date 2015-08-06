package com.example.student.stock;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;




public class MainActivity extends AppCompatActivity implements OnNavigationListener {


    ArrayList<StockData> list;
    MainAdapter ap;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //start 2 -----------------------------------
        prepareInitData();
        setContentView(R.layout.activity_main);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter();
        pager.setAdapter(pagerAdapter);

        String[] navis = {"관심종목1", "관심종목2", "관심종목3"};
        ArrayAdapter<String> list = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                navis);

        list.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(list, this);
        getSupportActionBar().setTitle("");


        //end 2 -------------------------------------------------
    }


    //가상 데이터 준비
    private void prepareInitData() {
        list=new ArrayList<StockData>();

        StockData data=new StockData();
        data.setCode("005930");
        data.setName("삼성전자");
        data.setLastTrade(773000);
        data.setStart(778000);
        data.setMax(794000);
        data.setMin(776000);
        data.setPrice(793000);
        data.setVolumn(2466142);
        list.add(data);

        data=new StockData();
        data.setCode("005380");
        data.setName("현대차");
        data.setLastTrade(160500);
        data.setStart(162000);
        data.setMax(163500);
        data.setMin(158500);
        data.setPrice(160000);
        data.setVolumn(1062797);
        list.add(data);

        data=new StockData();
        data.setCode("020560");
        data.setName("아시아나항공");
        data.setLastTrade(9540);
        data.setStart(9680);
        data.setMax(9770);
        data.setMin(9560);
        data.setPrice(9650);
        data.setVolumn(2503720);
        list.add(data);


    }
    
    class MyPagerAdapter extends PagerAdapter{


        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 3;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0==arg1;
        }
       
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
			//start 1 -----------------------------------

            ap = new MainAdapter(MainActivity.this, R.layout.main_row, list);
            ListView lv = new ListView(MainActivity.this);
            lv.setAdapter(ap);
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailMain.class);
                    startActivity(intent);
                }
            });
            container.addView(lv);
            return lv;
			//end 1 -----------------------------------------
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView((View)object);
        }

    }
    


    class MainAdapter extends ArrayAdapter<StockData> {
        private Context context;

        private ArrayList<StockData> datas;

        private int resId;

        public MainAdapter(Context context, int resource,
                           ArrayList<StockData> objects) {
            super(context, resource, objects);
            // TODO Auto-generated constructor stub
            this.context = context;
            this.datas = objects;
            this.resId = resource;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            MainWrapper wrapper;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(resId, null);

                wrapper = new MainWrapper(v);
                v.setTag(wrapper);
            }
            StockData d = datas.get(position);
            if (d != null) {
                wrapper = (MainWrapper) v.getTag();

                TextView nameView = wrapper.getNameView();
                nameView.setText(d.getName());

                TextView totalVolumnView = wrapper.getTotalVolumnView();
                totalVolumnView.setText(String.valueOf(d.getTotalVolumn()));

                StockPriceTextView changeProciceView = wrapper
                        .getChangePriceView();
                changeProciceView.setData(d);
                changeProciceView.setText(String.valueOf(d.getChange()));

                StockPriceTextView rateView = wrapper.getRateView();
                rateView.setData(d);
                rateView.setText(String.valueOf(d.getChangeRate()));

                StockPriceTextView priceView = wrapper.getPriceView();
                priceView.setData(d);
                priceView.setText(String.valueOf(d.getPrice()));

                BarGraphView bar = wrapper.getBarView();
                bar.setData(d);

                if (position % 2 == 1)
                    v.setBackgroundColor(0xff413c3c);
                else
                    v.setBackgroundColor(0xff000000);

            }
            return v;
        }

    }

    class MainWrapper {
        View base;
        TextView nameView;
        TextView totalVolumnView;
        BarGraphView barView;
        StockPriceTextView priceView;
        StockPriceTextView changePriceView;
        StockPriceTextView rateView;

        MainWrapper(View v) {
            base = v;
        }

        public TextView getNameView() {
            if (nameView == null)
                nameView = (TextView) base.findViewById(R.id.main_name);
            return nameView;
        }

        public TextView getTotalVolumnView() {
            if (totalVolumnView == null)
                totalVolumnView = (TextView) base
                        .findViewById(R.id.main_totalVolumn);
            return totalVolumnView;
        }

        public BarGraphView getBarView() {
            if (barView == null)
                barView = (BarGraphView) base.findViewById(R.id.main_bar);
            return barView;
        }

        public StockPriceTextView getPriceView() {
            if (priceView == null)
                priceView = (StockPriceTextView) base
                        .findViewById(R.id.main_price);
            return priceView;
        }

        public StockPriceTextView getChangePriceView() {
            if (changePriceView == null)
                changePriceView = (StockPriceTextView) base
                        .findViewById(R.id.main_changePrice);
            return changePriceView;
        }

        public StockPriceTextView getRateView() {
            if (rateView == null)
                rateView = (StockPriceTextView) base
                        .findViewById(R.id.main_rate);
            return rateView;
        }

    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // TODO Auto-generated method stub
        Log.d("kkang","onNavigationItemSelected:"+itemPosition);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


}

