package com.example.student.lbs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationListActivity extends ActionBarActivity implements OnItemClickListener{

    private DBAdapter dbAdapter;
    private ArrayList<FenceVO> fenceList;

    SimpleAdapter ap;


    ArrayList<HashMap<String, String>> adapterList = null;

    ListView lv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Auto-generated method stub
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        getFenceData();

        fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        getMenuInflater().inflate(R.menu.menu_locationlist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
//start 1 --------------------------------------------------------
            case R.id.menu_locationlist_settings:{
                Intent intent=new Intent(this,SettingMainActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_locationlist_add : {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("mode", MapActivity.MODE_ADD);
                startActivity(intent);
                break;
            }
            case R.id.menu_locationlist_history :{
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra("mode", MapActivity.MODE_HISTORY);
                startActivity(intent);
                break;
            }

//end 1 ----------------------------------------------
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.emo_im_yelling);
        builder.setTitle("알림");
        builder.setMessage("Location 확인/ 제거");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LocationListActivity.this, MapActivity.class);
                        intent.putExtra("mode", MapActivity.MODE_VIEW);
                        intent.putExtra("fence", fenceList.get(position));
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("제거",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                        dbAdapter.open();
                        dbAdapter.deleteTable("_id", fenceList.get(position).getFenceNo());
                        dbAdapter.close();

                        adapterList.remove(position);
                        ap.notifyDataSetChanged();


                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getFenceData() {
        fenceList = new ArrayList<FenceVO>();

        dbAdapter = new DBAdapter(this, DBAdapter.SQL_CREATE_FENCE, "fence");
        dbAdapter.open();

        Cursor cursor = dbAdapter.selectTable(new String[] { "_id","fence_name",
                        "fence_latitude", "fence_longitude", "fence_radius","fence_address" }, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {

            do {

                FenceVO f = new FenceVO();
                f.setFenceNo(cursor.getInt(0));
                f.setFenceName(cursor.getString(1));
                f.setFenceLatitude(cursor.getDouble(2));
                f.setFenceLongitude(cursor.getDouble(3));
                f.setFenceRadius(cursor.getInt(4));
                f.setFenceAddress(cursor.getString(5));
                Log.d("kkang","db address:"+cursor.getString(5));
                fenceList.add(f);
            } while (cursor.moveToNext());
        }



        dbAdapter.close();

    }

    private void fillData() {


        if (fenceList.size() == 0) {
            //start2 ---------------------------------------
            setContentView(R.layout.activity_locationlist_no_data);
            //end 2 ---------------------------------------

        } else {
            //start3 -----------------------------------
            setContentView(R.layout.activity_locationlist);
            //end 3 ---------------------------------------
            lv=(ListView)findViewById(R.id.activity_locationlist_list);
            lv.setOnItemClickListener(this);

            adapterList = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < fenceList.size(); i++) {
                FenceVO f = fenceList.get(i);

                HashMap<String, String> map=new HashMap<String, String>();
                map.put("name", f.getFenceName());
                map.put("address", f.getFenceAddress());

                adapterList.add(map);
            }
            ap=new SimpleAdapter(this,
                    adapterList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name","address"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );
            lv.setAdapter(ap);

        }
    }



}
