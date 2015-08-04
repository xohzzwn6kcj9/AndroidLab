package com.example.student.sms;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    SMSListAdapter adapter;
    RecyclerView recyclerView;
    ImageButton plusBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar  = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        plusBtn = (ImageButton) findViewById(R.id.plusBtn);
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(MainActivity.this, SMSWriteActivity.class);
                startActivity(intent);
            }
        });


        ArrayList<SMSMessage> items=new ArrayList<SMSMessage>();


        //code - dbms -
        SQLiteDatabase db = DbHelper.getInstance(this).getWritableDatabase();


        Cursor cursor= db.query(SmsTable.TABLE_NAME,new String[]{SmsTable.PHONE_NUMBER}, null, null, SmsTable.PHONE_NUMBER, null, "date desc");
        Log.d("kkang", "list cursor size:" + cursor.getCount());
        while(cursor.moveToNext()){

            String phoneNumber=cursor.getString(0);
            Cursor cursorDetail=db.query(SmsTable.TABLE_NAME,null,SmsTable.PHONE_NUMBER+"=?",new String[]{phoneNumber},null,null,"date desc","1");
            cursorDetail.moveToFirst();
            Log.d("kkang","detail:"+cursorDetail.getCount()+":"+cursorDetail.getLong(0)+":"+cursorDetail.getString(4));

            SMSMessage message=new SMSMessage();
            message.id=cursorDetail.getLong(0);
            message.phoneNumber=phoneNumber;
            message.date=SMSUtil.dateFormat(cursorDetail.getLong(2));
            message.state=cursorDetail.getInt(3);
            message.content=cursorDetail.getString(4);

            items.add(message);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new MyDecoration(this));
        adapter = new SMSListAdapter(this, recyclerView, items);
        recyclerView.setAdapter(adapter);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


class SMSListViewHolder extends RecyclerView.ViewHolder {

    public TextView phoneView;
    public TextView dateView;
    public TextView contentView;
    public RelativeLayout rootView;

    public SMSListViewHolder(View rootView) {
        super(rootView);
        this.rootView = (RelativeLayout) rootView;
        this.phoneView = (TextView) rootView.findViewById(R.id.list_item_phone);
        this.dateView = (TextView) rootView.findViewById(R.id.list_item_date);
        this.contentView = (TextView) rootView.findViewById(R.id.list_item_content);
    }
}
class SMSListAdapter extends RecyclerView.Adapter<SMSListViewHolder> {

    Context context;
    ArrayList<SMSMessage> items;
    RecyclerView recyclerView;

    public SMSListAdapter(Context context, RecyclerView recyclerView, ArrayList<SMSMessage> items){
        this.context = context;
        this.recyclerView = recyclerView;
        this.items = items;
    }

    @Override
    public SMSListViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View root = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, null);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SMSMessage message = (SMSMessage) v.getTag();
                Intent intent = new Intent(context, SMSReadActivity.class);
                intent.putExtra("mode", "list");
                intent.putExtra(SmsTable.PHONE_NUMBER, message.phoneNumber);
                context.startActivity(intent);
            }
        });
        return new SMSListViewHolder(root);
    }

    @Override
    public void onBindViewHolder(SMSListViewHolder smsListViewHolder, int i) {
        final SMSMessage message = items.get(i);
        smsListViewHolder.phoneView.setText(message.phoneNumber);
        smsListViewHolder.dateView.setText(message.date);
        smsListViewHolder.contentView.setText(message.content);
        smsListViewHolder.rootView.setTag(message);
    }

    @Override
    public int getItemCount() {
        return items==null ? 0 : items.size();
    }

}
class MyDecoration extends RecyclerView.ItemDecoration {
    final Context context;
    public MyDecoration(Context context){
        this.context = context;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, 10);
    }
}
